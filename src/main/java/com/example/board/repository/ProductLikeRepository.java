package com.example.board.repository;

import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductLike;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByProductAndMember(Product product, Member member);
    Optional<ProductLike> findByProductAndMember(Product product, Member member);
}
