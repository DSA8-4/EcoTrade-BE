package com.example.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.board.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, String>{
	 Optional<Member> findByEmail(String email);

}
