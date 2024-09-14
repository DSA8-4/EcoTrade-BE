package com.example.board.repository;

import com.example.board.model.chat.ChatRoom;
import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
	List<ChatRoom> findAllByProduct(Product product);
	ChatRoom findByProductAndMember(Product product, Member member);
	@Query("SELECT cr FROM ChatRoom cr WHERE cr.product.id = :productId AND cr.member.id = :memberId")
	ChatRoom findByProductIdAndMemberId(@Param("productId") Long productId, @Param("memberId") Long memberId);
}
