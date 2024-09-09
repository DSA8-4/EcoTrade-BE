package com.example.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.board.model.member.Member;
import com.example.board.model.product.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    // 예시로 Member의 ID로 Purchase를 찾는 메서드
    @Query("SELECT p FROM Purchase p WHERE p.buyer.member_id = :memberId")
    List<Purchase> findByBuyerId(@Param("memberId") String memberId);

}
