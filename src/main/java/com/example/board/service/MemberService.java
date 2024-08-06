package com.example.board.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.model.member.Member;
import com.example.board.repository.MemberRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberService {
	private MemberRepository memberRepository;
	
	@Autowired
	private void setMemberRepository(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	@Transactional
	public void saveMember(Member member) {
		memberRepository.save(member);
	}
	
	public Member findMemberById(String member_id) {
		Optional<Member> member = memberRepository.findById(member_id);
		return member.orElse(null);
	}
}