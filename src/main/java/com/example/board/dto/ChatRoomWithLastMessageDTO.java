package com.example.board.dto;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomWithLastMessageDTO {
    private Long id;
    private String name;
    private String lastMessage;
    private String imageUrl;
    private String timestamp;
    private String sender;
    
    public static ChatRoomWithLastMessageDTO fromEntity(ChatRoom chatRoom, ChatMessage lastMessage) {
        ChatRoomWithLastMessageDTO dto = new ChatRoomWithLastMessageDTO();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());

        if (lastMessage != null) {
            dto.setLastMessage(lastMessage.getContent());
            dto.setTimestamp(lastMessage.getTimestamp());
            dto.setSender(lastMessage.getSender());
        } else {
            dto.setLastMessage("No messages yet");
            dto.setTimestamp(null);
        }
        
        dto.setImageUrl(chatRoom.getProduct().getProductImages().get(0).getUrl());
        return dto;
    }
}
