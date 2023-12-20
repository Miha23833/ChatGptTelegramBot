package com.byoliee.bot.chat.handlers.plaintext;

import com.byoliee.bot.config.ChatGptConfiguration;
import com.byoliee.bot.db.DbMessageService;
import com.byoliee.bot.db.entities.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public void handle(PlainTextContext context) {
        dbMessageService.save(ChatMessage.fromTelegramMessage(context.event().getMessage()));
    }

    @Override
    public String getCommand() {
        return "chatgpt";
    }
}
