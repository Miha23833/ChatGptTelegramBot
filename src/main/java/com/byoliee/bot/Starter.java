package com.byoliee.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication()
public class Starter implements CommandLineRunner {
    private final PersyTelegramBot telegramBot;

    @Autowired
    public Starter(PersyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public static void main(String[] args) {
        SpringApplication.run(Starter.class);
    }

    @Override
    public void run(String... args) throws Exception {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);
    }
}
