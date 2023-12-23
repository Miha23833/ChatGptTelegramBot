package com.byoliee.bot.db.repositories;

import com.byoliee.bot.db.entities.TgChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<TgChatMessage, TgChatMessage.ChatMessageId> {
    List<TgChatMessage> findById_ChatId(long chatId);
    List<TgChatMessage> findById_ChatId(long chatId, Pageable pageable);
}
