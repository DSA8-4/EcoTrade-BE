package com.example.board.model.product;

import java.time.LocalDateTime;
import java.util.List;

import com.example.board.model.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long product_id;           // 상품 아이디
    
    private String title;              // 상품 제목
    private String contents;           // 상품 내용
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;             // 작성자 정보 (회원)

    private Long price;                // 상품 가격
    private Long hit = 0L;             // 상품 조회수 초기값 설정
    private Long heart = 0L;           // 상품 좋아요 수 초기값 설정
    private LocalDateTime created_time; // 상품 작성일
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AttachedImage> images; // 이미지 목록 추가
    
    // 조회수 증가 메서드
    public void addHit() {
        this.hit++;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_id=" + product_id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", price=" + price +
                ", hit=" + hit +
                ", heart=" + heart +
                ", created_time=" + created_time +
                ", imagesCount=" + (images != null ? images.size() : 0) +  // 이미지 개수 출력
                ", memberId=" + (member != null ? member.getMember_id() : "null") +  // member 정보 중 ID만 출력
                '}';
    }
}
