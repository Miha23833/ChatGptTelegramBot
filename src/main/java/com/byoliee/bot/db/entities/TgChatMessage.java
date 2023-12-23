package com.byoliee.bot.db.entities;

import com.theokanning.openai.completion.chat.ChatMessage;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class TgChatMessage {
    public static TgChatMessage fromTelegramMessage(Message message, String role) {
        return new TgChatMessage(new ChatMessageId(message.getChatId(), message.getMessageId()), message.getText(), role);
    }

    @EmbeddedId
    private ChatMessageId id;

    @Getter@Setter
    private String content;
    @Getter@Setter
    private String role;

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

    public ChatMessage toChatMessage() {
        return new ChatMessage(this.role, this.content);
    }
}
