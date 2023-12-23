package com.byoliee.bot.db;

import com.byoliee.bot.db.entities.TgChatMessage;
import com.byoliee.bot.db.repositories.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DbMessageService {
    private final ChatMessageRepository repository;

    @Autowired
    public DbMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public TgChatMessage save(TgChatMessage tgChatMessage) {
        return repository.save(tgChatMessage);
    }

    public List<TgChatMessage> getAll(long id) {
        return repository.findById_ChatId(id);
    }

    public List<TgChatMessage> getAll(long id, int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("id.messageId").descending());
        return repository.findById_ChatId(id, pageable);
    }
}
