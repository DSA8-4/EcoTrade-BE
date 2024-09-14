package com.example.board.model.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class CreateRoomRequest {
    private Long productId;
    private String memberId;
}
