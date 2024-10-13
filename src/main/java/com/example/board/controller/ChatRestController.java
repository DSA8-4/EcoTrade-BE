package com.example.board.controller;

import com.example.board.dto.ChatMessageDTO;
import com.example.board.dto.ChatRoomDTO;
import com.example.board.dto.ChatRoomWithLastMessageDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
	public ResponseEntity<ChatRoomDTO> createRoom(@RequestBody CreateRoomRequest request,
												  @RequestHeader("Authorization") String authorizationHeader) {
	    String token = authorizationHeader.replace("Bearer ", "");
	    
	    if (!jwtTokenProvider.validateToken(token)) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	    }
	    
	    log.info("chatroom: {}",request.getMemberId());
	    log.info("chatroom: {}",request.getProductId());
	    String memberId = request.getMemberId();
	    Product product = productService.findProduct(request.getProductId());
	    Member member = memberService.findMemberById(memberId);

	    if (product == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }

	    try {
			ChatRoomDTO chatRoomDTO = chatService.createChatRoom(product, member);


			return ResponseEntity.ok(chatRoomDTO);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	@GetMapping("/rooms/list")
	public ResponseEntity<List<ChatRoomWithLastMessageDTO>> getUserChatRooms(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer ", "");

		if (!jwtTokenProvider.validateToken(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String memberId = jwtTokenProvider.getMemberIdFromToken(token);
		Member member = memberService.findMemberById(memberId);

		if (member == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		List<ChatRoom> chatRooms = chatService.getChatRoomsForMember(member);
		List<ChatRoomWithLastMessageDTO> chatRoomDTOs = chatRooms.stream()
				.map(chatRoom -> {
					ChatMessage lastMessage = chatService.getLastMessageForChatRoom(chatRoom);
					ChatRoomWithLastMessageDTO dto = ChatRoomWithLastMessageDTO.fromEntity(chatRoom, lastMessage);
					int unreadCount = chatService.getUnreadMessageCount(chatRoom, memberId);
					dto.setUnreadCount(unreadCount);
					return dto;
				})
				.collect(Collectors.toList());
		
		log.info("chatRoomDTOs", chatRoomDTOs);
		return ResponseEntity.ok(chatRoomDTOs);
	}
	
    @GetMapping("/rooms/{productId}")
    public ResponseEntity<List<ChatRoomWithLastMessageDTO>> getCertainProductChatRoom(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("productId") Long productId) {
        
        String token = authorizationHeader.replace("Bearer ", "");

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

		String memberId = jwtTokenProvider.getMemberIdFromToken(token);
        List<ChatRoom> chatRooms = chatService.getChatRoomsByProductId(productId);

        List<ChatRoomWithLastMessageDTO> chatRoomDTOs = chatRooms.stream()
                .map((ChatRoom chatRoom) -> {
					ChatMessage lastMessage = chatService.getLastMessageForChatRoom(chatRoom);
					ChatRoomWithLastMessageDTO dto = ChatRoomWithLastMessageDTO.fromEntity(chatRoom, lastMessage);
					int unreadCount = chatService.getUnreadMessageCount(chatRoom, memberId);
					dto.setUnreadCount(unreadCount);
					return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatRoomDTOs);
    }

	@GetMapping("rooms/{roomId}/messages")
	public ResponseEntity<List<ChatMessageDTO>> getMessagesForRoom(@PathVariable("roomId") Long roomId,
																   @RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.replace("Bearer ", "");

		if (!jwtTokenProvider.validateToken(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String memberId = jwtTokenProvider.getMemberIdFromToken(token);
		ChatRoom chatRoom = chatService.getRoomById(roomId);
		if (chatRoom == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		List<ChatMessageDTO> messages = chatService.getMessagesForChatRoom(roomId);
		chatService.markMessagesAsRead(chatRoom, memberId);
		return ResponseEntity.ok(messages);
	}

	@DeleteMapping("/rooms/{roomId}")
	public ResponseEntity<Void> deleteChatRoomIfEmpty(@PathVariable("roomId") Long roomId) {
		ChatRoom chatRoom = chatService.getRoomById(roomId);

		if (chatRoom == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		List<ChatMessageDTO> messages = chatService.getMessagesForChatRoom(roomId);

		if (messages.isEmpty()) {
			chatService.deleteChatRoom(chatRoom);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
}
