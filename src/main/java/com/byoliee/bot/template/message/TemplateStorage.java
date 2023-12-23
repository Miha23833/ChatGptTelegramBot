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
            Resource resource = resourceLoader.getResource("classpath:" + templatesDir + File.separator + templateName);
            Path path = resource.getFile().toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
