package com.example.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.board.model.chat.ChatMessage;
import com.example.board.model.chat.ChatRoom;
import com.example.board.repository.ChatMessageRepository;
import com.example.board.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getRoom(String name) {
        return chatRoomRepository.findByName(name);
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