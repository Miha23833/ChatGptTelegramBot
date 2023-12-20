package com.byoliee.bot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateStorageConfig {

    @Value("${messageTemplates.dir}")
    @Getter
    private String templatesDir;

}
