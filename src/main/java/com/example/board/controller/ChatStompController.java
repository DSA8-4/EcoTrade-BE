package com.example.board.controller;

import com.example.board.dto.ChatMessageDTO;
import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("chat")
@MessageMapping("chat")
@RequiredArgsConstructor
@Slf4j
public class ChatStompController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send/{roomId}")
    public void sendMessage(@Payload ChatMessageDTO messageDTO,
                            @DestinationVariable("roomId") Long roomId) {
        ChatRoom chatRoom = chatService.getRoomById(roomId);
        if (chatRoom == null) {
            log.error("Chat room not found for roomId: {}", roomId);
            return;
        }

        ChatMessage message = new ChatMessage();
        message.setContent(messageDTO.getContent());
        message.setSender(messageDTO.getSender());
        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        message.setChatRoom(chatRoom);

        chatService.saveMessage(message);

        // Convert the saved message to DTO to avoid entity-related issues
        ChatMessageDTO responseMessage = ChatMessageDTO.fromEntity(message);

        messagingTemplate.convertAndSend("/sub/chatroom/" + roomId, responseMessage);
    }
}