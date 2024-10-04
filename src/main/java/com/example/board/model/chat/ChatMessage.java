package com.example.board.model.chat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String sender;
    private String timestamp;
    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name="chat_room_id")
    @JsonBackReference
    private ChatRoom chatRoom;
}