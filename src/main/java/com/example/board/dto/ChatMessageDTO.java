package com.example.board.dto;

import com.example.board.model.chat.ChatMessage;
import lombok.Data;

@Data
public class ChatMessageDTO {
    private Long id;
    private String content;
    private String sender;
    private String timestamp;
    private boolean read;
    public static ChatMessageDTO fromEntity(ChatMessage chatMessage) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(chatMessage.getId());
        dto.setContent(chatMessage.getContent());
        dto.setSender(chatMessage.getSender());
        dto.setTimestamp(chatMessage.getTimestamp());
        dto.setRead(chatMessage.isRead());
        return dto;
    }
}
