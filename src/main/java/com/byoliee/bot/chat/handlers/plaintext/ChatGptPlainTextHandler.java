package com.byoliee.bot.chat.handlers.plaintext;

import com.byoliee.bot.config.ChatGptConfiguration;
import com.byoliee.bot.db.DbMessageService;
import com.byoliee.bot.db.entities.TgChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.subscribers.DefaultSubscriber;
import lombok.SneakyThrows;
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
    private final OpenAiService openAiService;
    private final DbMessageService dbMessageService;
    private final int maxHistoryLength;
    private final int streamMessageUpdateMillis;

    @Autowired
    public ChatGptPlainTextHandler(OpenAiService openAiService, DbMessageService dbMessageService, ChatGptConfiguration chatGptConfiguration) {
        this.openAiService = openAiService;
        this.dbMessageService = dbMessageService;

        this.maxHistoryLength = chatGptConfiguration.getMaxHistoryLength();
        this.streamMessageUpdateMillis = chatGptConfiguration.getStreamMessageUpdateMillis();
    }

    @Override
    public void handle(PlainTextContext context) throws TelegramApiException {
        saveMessageToDb(TgChatMessage.fromTelegramMessage(context.event().getMessage(), ChatMessageRole.USER.value()));
        List<TgChatMessage> history = getLastMessages(context.event().getMessage().getChatId());
        Collections.reverse(history);

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
                .model("gpt-3.5-turbo")
                .stream(true).build();
    }

    private Message sendFeatureAnswer(long chatId) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("...")
                .build();
        return bot.execute(sendMessage);
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
            if (text != null) {
                builder.append(text);
            }
            if (System.currentTimeMillis() - lastUpdatedMessageTimeMillis >= ChatGptPlainTextHandler.this.streamMessageUpdateMillis) {
                EditMessageText edit = createEditMessageText()
                        .text(builder.toString())
                        .build();
                updateMessage(edit);
                lastUpdatedMessageTimeMillis = System.currentTimeMillis();
            }
        }

        @Override
        public void onError(Throwable t) {
            EditMessageText edit = createEditMessageText()
                    .text("Произошла непредвиденная ошибка")
                    .build();
            updateMessage(edit);
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

        @SneakyThrows
        private void updateMessage(EditMessageText edit) {
//            edit.enableMarkdown(true);
            ChatGptPlainTextHandler.this.bot.execute(edit);
        }

        private EditMessageText.EditMessageTextBuilder createEditMessageText() {
            return EditMessageText.builder()
                    .messageId(this.messageId)
                    .chatId(this.chatId);
        }
    }
}
