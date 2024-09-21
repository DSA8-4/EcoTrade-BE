package com.example.board.repository;

import com.example.board.model.member.Member;
import com.example.board.model.member.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    ProfileImage findByMember(Member member);
}
