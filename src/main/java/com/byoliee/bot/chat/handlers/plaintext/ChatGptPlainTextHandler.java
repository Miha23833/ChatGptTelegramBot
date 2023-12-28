package com.byoliee.bot.chat.handlers.plaintext;

import com.byoliee.bot.config.ChatGptConfiguration;
import com.byoliee.bot.db.DbMessageService;
import com.byoliee.bot.db.entities.TgChatMessage;
import com.didalgo.gpt3.ModelType;
import com.didalgo.gpt3.TokenCount;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.subscribers.DefaultSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class ChatGptPlainTextHandler extends PlainTextHandler {
    private static final Logger logger = LoggerFactory.getLogger(ChatGptPlainTextHandler.class);

    private final OpenAiService openAiService;
    private final DbMessageService dbMessageService;
    private final int maxHistoryLength;
    private final int streamMessageUpdateMillis;
    private final int maxTokens;

    @Autowired
    public ChatGptPlainTextHandler(OpenAiService openAiService, DbMessageService dbMessageService, ChatGptConfiguration chatGptConfiguration) {
        this.openAiService = openAiService;
        this.dbMessageService = dbMessageService;

        this.maxHistoryLength = chatGptConfiguration.getMaxHistoryLength();
        this.streamMessageUpdateMillis = chatGptConfiguration.getStreamMessageUpdateMillis();

        this.maxTokens = chatGptConfiguration.getMaxTokens();
    }

    @Override
    public void handle(PlainTextContext context) throws TelegramApiException {
        saveMessageToDb(TgChatMessage.fromTelegramMessage(context.event().getMessage(), ChatMessageRole.USER.value()));
        List<TgChatMessage> history = getLastMessages(context.event().getMessage().getChatId());
        Collections.reverse(history);
        trimMessagesToTokenLimit(history, this.maxTokens);

        Message answer = sendFeatureAnswer(context.chatId());

        ChatCompletionRequest completionRequest = buildChatCompletionRequest(convertToChatMessage(history));
        openAiService.streamChatCompletion(completionRequest).subscribeWith(new MessageStreamSubscriber(answer));
    }

    @Override
    public String getCommand() {
        return "chatgpt";
    }

    private void saveMessageToDb(TgChatMessage tgChatMessage) {
        dbMessageService.save(tgChatMessage);
    }

    private List<TgChatMessage> getLastMessages(long chatId) {
        return dbMessageService.getAll(chatId, maxHistoryLength);
    }

    private List<ChatMessage> convertToChatMessage(List<TgChatMessage> tgChatMessages) {
        return tgChatMessages.stream().map(TgChatMessage::toChatMessage).toList();
    }

    private ChatCompletionRequest buildChatCompletionRequest(List<ChatMessage> chatMessages) {
        return ChatCompletionRequest.builder()
                .messages(chatMessages)
                .model(ModelType.GPT_3_5_TURBO.modelName())
                .stream(true).build();
    }

    private Message sendFeatureAnswer(long chatId) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("...")
                .build();
        return bot.execute(sendMessage);
    }

    private void trimMessagesToTokenLimit(List<TgChatMessage> messages, int maxTokens) {
        int totalTokens = calculateTotalTokens(messages);

        while (totalTokens > maxTokens && !messages.isEmpty()) {
            messages.remove(messages.size() - 1);
            totalTokens = calculateTotalTokens(messages);
        }
    }

    private int calculateTotalTokens(List<TgChatMessage> messages) {
        return messages.stream()
                .mapToInt(message -> TokenCount.fromString(message.getContent(), ModelType.GPT_3_5_TURBO.getTokenizer()))
                .sum();
    }

    private class MessageStreamSubscriber extends DefaultSubscriber<ChatCompletionChunk> {
        private final StringBuilder builder;
        private final long chatId;
        private final int messageId;
        private long lastUpdatedMessageTimeMillis;

        private MessageStreamSubscriber(Message message) {
            this.builder = new StringBuilder();
            this.chatId = message.getChatId();
            this.messageId = message.getMessageId();
            lastUpdatedMessageTimeMillis = System.currentTimeMillis();
        }

        @Override
        public void onNext(ChatCompletionChunk chatCompletionChunk) {
            String text = chatCompletionChunk.getChoices().get(0).getMessage().getContent();
            if (text != null && text.length() > 0) {
                builder.append(text);
            }
            if (System.currentTimeMillis() - lastUpdatedMessageTimeMillis >= ChatGptPlainTextHandler.this.streamMessageUpdateMillis) {
                if (!builder.isEmpty()) {
                    EditMessageText edit = createEditMessageText()
                            .text(builder.toString())
                            .build();
                    updateMessage(edit);
                    lastUpdatedMessageTimeMillis = System.currentTimeMillis();
                }
            }
        }

        @Override
        public void onError(Throwable t) {
            EditMessageText edit = createEditMessageText()
                    .text("Произошла непредвиденная ошибка")
                    .build();
            updateMessage(edit);
            logger.error(t.getMessage(), t);
            throw new RuntimeException(t);
        }

        @Override
        public void onComplete() {
            dbMessageService.save(new TgChatMessage(new TgChatMessage.ChatMessageId(chatId, messageId), builder.toString(), ChatMessageRole.ASSISTANT.value()));
            EditMessageText edit = createEditMessageText()
                    .text(builder.toString())
                    .build();
            edit.enableMarkdown(true);
            updateMessage(edit);
        }

        private void updateMessage(EditMessageText edit) {
            try {
                ChatGptPlainTextHandler.this.bot.execute(edit);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        private EditMessageText.EditMessageTextBuilder createEditMessageText() {
            return EditMessageText.builder()
                    .messageId(this.messageId)
                    .chatId(this.chatId);
        }
    }
}
