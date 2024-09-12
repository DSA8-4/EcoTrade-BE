package com.example.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.board.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
	Optional<Member> findByEmail(String email);

	 @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END FROM Member m WHERE m.member_id = :member_id")
	    boolean existsByMember_id(@Param("member_id") String member_id);
	// email 중복 여부 확인
	boolean existsByEmail(String email);

	// name 중복 여부 확인
	boolean existsByName(String name);

}
