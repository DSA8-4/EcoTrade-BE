package com.example.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.product.Product;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	ChatRoom findByProduct(Product product); //
}
