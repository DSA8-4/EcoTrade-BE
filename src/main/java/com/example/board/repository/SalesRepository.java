package com.example.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.board.model.product.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {

	  // 판매자 ID로 판매 내역을 조회하는 메서드
	@Query("SELECT s FROM Sales s WHERE s.seller.id = :memberId")
    List<Sales> findBySellerId(@Param("memberId") String memberId);

}
