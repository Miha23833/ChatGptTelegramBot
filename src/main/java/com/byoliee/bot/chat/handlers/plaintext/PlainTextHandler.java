package com.byoliee.bot.chat.handlers.plaintext;

import com.byoliee.bot.PersyTelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class PlainTextHandler {
    @Autowired
    @Lazy
    protected PersyTelegramBot bot;

    public abstract void handle(PlainTextContext context) throws TelegramApiException;

    public abstract String getCommand();
}
