package com.example.board.repository;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    ChatMessage findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);
}