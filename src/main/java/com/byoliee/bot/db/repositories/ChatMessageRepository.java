package com.byoliee.bot.db.repositories;

import com.byoliee.bot.db.entities.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, ChatMessage.ChatMessageId> {
    List<ChatMessage> findById_ChatId(long chatId);
    List<ChatMessage> findTopById_ChatIdOrderByIdMessageIdDesc(long chatId, Pageable pageable);
}
