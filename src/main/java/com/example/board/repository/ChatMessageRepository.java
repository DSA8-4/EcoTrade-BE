package com.example.board.repository;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom(ChatRoom chatRoom);
    List<ChatMessage> findByChatRoomId(Long chatRoomId);

    ChatMessage findTopByChatRoomOrderByIdDesc(ChatRoom chatRoom);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE cm.chatRoom = :chatRoom AND cm.sender <> :memberId")
    int markMessagesAsReadForMember(@Param("chatRoom") ChatRoom chatRoom, @Param("memberId") String memberId);
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom = :chatRoom AND m.isRead = false AND m.sender != :memberId")
    int countUnreadMessagesByChatRoomAndSenderNot(@Param("chatRoom") ChatRoom chatRoom, @Param("memberId") String memberId);
}