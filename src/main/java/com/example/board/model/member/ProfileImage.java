package com.example.board.model.member;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key for the ProfileImage entity

    @Column(length = 1024)
    private String url; // URL of the profile image

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member; // Reference back to the Member entity

    public ProfileImage() {}

    public ProfileImage(String url, Member member) {
        this.url = url;
        this.member = member;
    }
   
}
