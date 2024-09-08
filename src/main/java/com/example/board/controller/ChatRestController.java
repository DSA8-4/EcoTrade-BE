package com.example.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@PostMapping("rooms/createRoom")
	public ResponseEntity<ChatRoom> createRoom(@RequestBody String roomName) {
	    ChatRoom existingRoom = chatService.getRoom(roomName);
	    
	    if (existingRoom != null) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409 Conflict
	    }

	    ChatRoom newRoom = chatService.createRoom(roomName);
	    return ResponseEntity.ok(newRoom); // 200 OK with the new room details
	}
	
	@GetMapping("/rooms")
	public ResponseEntity<List<ChatRoom>> getAllRooms() {
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
