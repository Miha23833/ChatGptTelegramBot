package com.byoliee.bot.chat.handlers.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class StartCommandHandler extends CommandHandler {
    @Autowired
    public StartCommandHandler() {
    }

    @Override
    public void handle(CommandContext context) throws TelegramApiException {
        SendMessage msg = new SendMessage(String.valueOf(context.chatId()), """
*Этот бот базируется на Open AI ChatGPT 3.5 API.*

Его основная задача - отвечать на вопросы пользователя посредством __*ChatGPT*__.

Особенность его состоит в том, что он использует предыдущие сообщения
пользователя как материал для постройки контекста диалога, что даёт возможность отвечать более точно и раскрыто.

__*Отправляя следующее сообщение, вы соглашаетесь с тем, что эта переписка будет сохранена в базе данных*__""");
        msg.enableMarkdown(true);
        this.bot.execute(msg);
    }

    @Override
    public String getCommand() {
        return "start";
    }
}
