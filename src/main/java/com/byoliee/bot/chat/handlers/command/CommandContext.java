package com.byoliee.bot.chat.handlers.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public record CommandContext(String chatId, Update event, String[] args) {
}
