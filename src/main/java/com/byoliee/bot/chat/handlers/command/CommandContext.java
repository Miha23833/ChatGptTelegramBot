package com.byoliee.bot.chat.handlers.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public record CommandContext(long chatId, Update event, String[] args) {
}
