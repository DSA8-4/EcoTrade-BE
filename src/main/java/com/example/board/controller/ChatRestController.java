package com.example.board.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatRestController {
	
    private final ChatService chatService;
    
    @GetMapping("/rooms")
    public  ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }
    
    @GetMapping("/rooms/{id}")
    public ChatRoom getChatRoom(@PathVariable("room_name") Long id) {
    	return chatService.getRoomById(id);
    }
    
    @GetMapping("messages/{room_id}")
    public ResponseEntity<List<ChatMessage>> getChatRoomMessages(@PathVariable("room_id") Long id) {
    	List<ChatMessage> messages = chatService.getMessagesByChatRoomId(id);
        return ResponseEntity.ok(messages);
    }
}
