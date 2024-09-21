package com.example.board.service;

import com.example.board.dto.ChatMessageDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<ChatRoom> getChatRoomsForMember(Member member) {
        List<ChatRoom> roomsAsMember = chatRoomRepository.findByMember(member);
        List<ChatRoom> roomsAsProductOwner = chatRoomRepository.findByProduct_Member(member);
        List<ChatRoom> allRooms = new ArrayList<>();
        allRooms.addAll(roomsAsMember);
        allRooms.addAll(roomsAsProductOwner);

        return allRooms;
    }

    public String getLastMessageForChatRoom(ChatRoom chatRoom) {
        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderByIdDesc(chatRoom);
        return lastMessage != null ? lastMessage.getContent() : null;
    }

    public List<ChatMessageDTO> getMessagesForChatRoom(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(chatRoomId);
        return messages.stream()
                .map(ChatMessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ChatRoom getRoomById(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public void deleteChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.delete(chatRoom);
    }
}