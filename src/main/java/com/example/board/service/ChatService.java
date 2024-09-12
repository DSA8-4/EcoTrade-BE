package com.example.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.example.board.repository.ChatMessageRepository;
import com.example.board.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createChatRoom(Product product, Member member) {
        // Check if this member has already created a chat room for this product
        ChatRoom existingRoom = chatRoomRepository.findByProductAndMember(product, member);
        if (existingRoom != null) {
            throw new IllegalStateException("This member already has a chat room for this product.");
        }
        
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(product.getTitle());
        chatRoom.setProduct(product);
        chatRoom.setMember(member); // Set the member creating the room
        
        return chatRoomRepository.save(chatRoom);
    }

    // Remove the check for existing room by product
    public List<ChatRoom> getRoomsByProduct(Product product) {
        return chatRoomRepository.findAllByProduct(product);
    }

    public ChatRoom getRoomById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }
    
    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }

    public List<ChatMessage> getMessagesByChatRoomId(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomId(chatRoomId);
    }
    
    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }
}