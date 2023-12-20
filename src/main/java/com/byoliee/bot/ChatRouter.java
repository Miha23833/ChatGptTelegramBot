package com.byoliee.bot;

import com.byoliee.bot.chat.handlers.command.CommandContext;
import com.byoliee.bot.chat.handlers.command.CommandHandler;
import com.byoliee.bot.chat.handlers.plaintext.ChatGptPlainTextHandler;
import com.byoliee.bot.chat.handlers.plaintext.PlainTextContext;
import com.byoliee.bot.chat.handlers.plaintext.PlainTextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatRouter {
    private final Map<String, CommandHandler> commandHandlers;
    private final PlainTextHandler plainTextHandler;

    @Autowired
    public ChatRouter(List<CommandHandler> handlerList, ChatGptPlainTextHandler chatGptPlainTextHandler) {
        Map<String, CommandHandler> handlers = new HashMap<>();
        for (CommandHandler handler : handlerList) {
            handlers.put(handler.getCommand(), handler);
        }
        this.commandHandlers = Collections.unmodifiableMap(handlers);
        this.plainTextHandler = chatGptPlainTextHandler;
    }

    public void handleCommand(String command, CommandContext context) {
        try {
            CommandHandler handler = commandHandlers.get(command.trim().toLowerCase());
            if (handler != null) {
                handler.handle(context);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handePlainText(PlainTextContext context) {
        try {
            plainTextHandler.handle(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
