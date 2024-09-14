package com.example.board.service;

import com.example.board.dto.ChatRoomDTO;
import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.example.board.repository.ChatMessageRepository;
import com.example.board.repository.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public ChatRoomDTO createChatRoom(Product product, Member member) {
        ChatRoom existingRoom = chatRoomRepository.findByProductAndMember(product, member);
        if (existingRoom != null) {
            return ChatRoomDTO.fromEntity(existingRoom);
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(product.getTitle());
        chatRoom.setProduct(product);
        chatRoom.setMember(member);
        
        return ChatRoomDTO.fromEntity(chatRoomRepository.save(chatRoom));
    }

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