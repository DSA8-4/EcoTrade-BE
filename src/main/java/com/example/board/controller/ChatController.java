package com.example.board.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send/{room}")
    public void sendMessage(@Payload ChatMessage message, @DestinationVariable("room") String room) {
        ChatRoom chatRoom = chatService.getRoom(room);
        log.info("room: {}", chatRoom);
        message.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        message.setChatRoom(chatRoom);
        chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/" + room, message);
    }

    @MessageMapping("/chat.createRoom")
    @SendTo("/topic/rooms")
    public void createRoom(@Payload String roomName) {
    	ChatRoom existRoom = chatService.getRoom(roomName);
    	if(existRoom == null) {
    		chatService.createRoom(roomName);
    	}
    }
}