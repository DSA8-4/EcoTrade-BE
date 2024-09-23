package com.example.board.dto;

import com.example.board.model.chat.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomWithLastMessageDTO {
    private Long id;
    private String name;
    private String lastMessage;

    public static ChatRoomWithLastMessageDTO fromEntity(ChatRoom chatRoom, String lastMessage) {
        ChatRoomWithLastMessageDTO dto = new ChatRoomWithLastMessageDTO();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());
        dto.setLastMessage(lastMessage);
        return dto;
    }
}
