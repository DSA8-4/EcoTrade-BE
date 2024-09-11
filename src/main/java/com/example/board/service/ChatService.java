package com.example.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.model.product.Product;
import com.example.board.repository.ChatMessageRepository;
import com.example.board.repository.ChatRoomRepository;
import com.example.board.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;


    public ChatRoom createChatRoom(Product product) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(product.getTitle()); // Automatically set room name to product title
        chatRoom.setProduct(product);

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getRoomByProduct(Product product) {
        return chatRoomRepository.findByProduct(product);
    }

    public ChatRoom getRoom(Product product) {
        return chatRoomRepository.findByProduct(product);
    }

    public ChatRoom getRoomById(Long id) {
    	return chatRoomRepository.findById(id).get();
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