package com.byoliee.bot.chat.handlers.plaintext;

import org.telegram.telegrambots.meta.api.objects.Update;

public record PlainTextContext(String chatId, Update event, String text) {}
