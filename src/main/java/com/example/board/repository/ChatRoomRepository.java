package com.example.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	List<ChatRoom> findAllByProduct(Product product); 
	ChatRoom findByProductAndMember(Product product, Member member); 
}
