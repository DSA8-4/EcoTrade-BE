package com.example.board.model.chat;

import com.example.board.model.member.Member;
import com.example.board.model.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "chat_room", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "member_id"})
})
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}