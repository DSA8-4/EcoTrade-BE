package com.example.board.dto;

import com.example.board.model.chat.ChatRoom;
import lombok.Data;

@Data
public class ChatRoomDTO {
    private Long id;
    private String name;

    public static ChatRoomDTO fromEntity(ChatRoom chatRoom) {
        ChatRoomDTO dto = new ChatRoomDTO();
        dto.setId(chatRoom.getId());
        dto.setName(chatRoom.getName());
        return dto;
    }
}