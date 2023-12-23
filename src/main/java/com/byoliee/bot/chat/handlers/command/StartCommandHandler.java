package com.byoliee.bot.chat.handlers.command;

import com.byoliee.bot.template.message.TemplateStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class StartCommandHandler extends CommandHandler {
    private final String answerTemplate;

    @Autowired
    public StartCommandHandler(TemplateStorage templateStorage) {
        this.answerTemplate = templateStorage.getTemplate("StartCommandAnswer");
    }

    @Override
    public void handle(CommandContext context) throws TelegramApiException {
        SendMessage msg = new SendMessage(String.valueOf(context.chatId()), answerTemplate);
        msg.enableMarkdown(true);
        this.bot.execute(msg);
    }

    @Override
    public String getCommand() {
        return "start";
    }
}
