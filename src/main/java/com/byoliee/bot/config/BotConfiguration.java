package com.byoliee.bot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfiguration {
    @Getter
    @Value("${api.token}")
    private String token;
}
