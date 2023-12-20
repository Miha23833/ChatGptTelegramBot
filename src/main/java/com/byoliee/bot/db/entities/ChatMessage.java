package com.byoliee.bot.db.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @EmbeddedId
    private ChatMessageId id;

    @Getter@Setter
    private String content;

    public ChatMessage() {}
    public ChatMessage(ChatMessageId id, String content) {
        this.id = id;
        this.content = content;
    }

    public static class ChatMessageId implements Serializable {
        @Getter
        private long chatId;
        @Getter
        private long messageId;

        public ChatMessageId () {}

        public ChatMessageId(long chatId, long messageId) {
            this.chatId = chatId;
            this.messageId = messageId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChatMessageId that = (ChatMessageId) o;
            return chatId == that.chatId && messageId == that.messageId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(chatId, messageId);
        }
    }

    public static ChatMessage fromTelegramMessage(Message message) {
        return new ChatMessage(new ChatMessageId(message.getChatId(), message.getMessageId()), message.getText());
    }
}
