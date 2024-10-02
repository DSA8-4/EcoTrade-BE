package com.example.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.product.Purchase;

public interface EcoProductPurchaseRepository extends JpaRepository<EcoProductPurchase, Long> {
	
	 @Query("SELECT p FROM EcoProductPurchase p WHERE p.buyer.member_id = :memberId")
	    List<EcoProductPurchase> findByBuyerId(@Param("memberId") String memberId);
}
