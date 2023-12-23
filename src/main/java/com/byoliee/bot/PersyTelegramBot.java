package com.byoliee.bot;

import com.byoliee.bot.chat.handlers.command.CommandContext;
import com.byoliee.bot.chat.handlers.plaintext.PlainTextContext;
import com.byoliee.bot.config.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class PersyTelegramBot extends TelegramLongPollingBot {
    private final ChatRouter chatRouter;

    @Autowired
    public PersyTelegramBot(BotConfiguration botConfiguration, ChatRouter chatRouter) {
        super(botConfiguration.getToken());
        this.chatRouter = chatRouter;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            long chatId = message.getChatId();
            String text = message.getText();

            if (text.startsWith("/")) {
                if (text.length() > 2) {
                    text = text.substring(1);
                    String[] commandAndArgs = text.split(" ", 2);
                    String command = commandAndArgs[0];
                    String[] args = commandAndArgs.length > 1 ? commandAndArgs[1].split("\\s") : new String[0];

                    CommandContext context = new CommandContext(chatId, update, args);

                    chatRouter.handleCommand(command, context);
                }
            } else {
                PlainTextContext context = new PlainTextContext(chatId, update, text);
                chatRouter.handePlainText(context);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "Persy";
    }
}
