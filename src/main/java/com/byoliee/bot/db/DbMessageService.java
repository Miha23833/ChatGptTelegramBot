package com.byoliee.bot.db;

import com.byoliee.bot.db.entities.ChatMessage;
import com.byoliee.bot.db.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbMessageService {
    private final ChatMessageRepository repository;

    @Autowired
    public DbMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage save(ChatMessage chatMessage) {
        return repository.save(chatMessage);
    }

    public List<ChatMessage> getAll(long id) {
        return repository.findById_ChatId(id);
    }

    public List<ChatMessage> getAll(long id, int limit) {
        return repository.findTopById_ChatIdOrderByIdMessageIdDesc(id, Pageable.ofSize(limit));
    }
}
