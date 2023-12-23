package com.byoliee.bot.template.message;

import com.byoliee.bot.config.TemplateStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TemplateStorage {
    private final ResourceLoader resourceLoader;
    private final String templatesDir;

    @Autowired
    public TemplateStorage(ResourceLoader resourceLoader, TemplateStorageConfig config) {
        this.resourceLoader = resourceLoader;
        this.templatesDir = config.getTemplatesDir();
    }

    public String getTemplate(String templateName) {
        try {
            Path path = Paths.get(templatesDir, templateName);
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
