package com.example.board.repository;

import com.example.board.model.member.Member;
import com.example.board.model.product.ProductLike;
import com.example.board.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByProductAndMember(Product product, Member member);
}
