package com.example.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.model.chat.CreateRoomRequest;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.example.board.service.ChatService;
import com.example.board.service.MemberService;
import com.example.board.service.ProductService;
import com.example.board.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatRestController {

	private final ChatService chatService;
	private final ProductService productService;
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("rooms/createRoom")
	public ResponseEntity<Void> createRoom(@RequestBody CreateRoomRequest request,
	                                       @RequestHeader("Authorization") String authorizationHeader) {
	    String token = authorizationHeader.replace("Bearer ", "");
	    
	    if (!jwtTokenProvider.validateToken(token)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
	    log.info("chatroom: {}",request.getMemberId());
	    log.info("chatroom: {}",request.getProductId());
//	    String memberId = request.getMemberId();
//	    Product product = productService.findProduct(request.getProductId());
//	    Member member = memberService.findMemberById(memberId);
//	    
//	    if (product == null) {
//	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//	    }
//
//	    try {
//	        chatService.createChatRoom(product, member);
//	        return ResponseEntity.ok().build();
//	    } catch (IllegalStateException e) {
//	        return ResponseEntity.status(HttpStatus.CONFLICT).build();
//	    }
	    return ResponseEntity.ok().build();
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
