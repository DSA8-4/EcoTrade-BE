package com.example.board.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
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
	    public void saveMember(MemberJoinForm memberJoinForm) {
	        // MemberJoinForm을 Member 엔티티로 변환
	        Member member = new Member();
	        member.setMember_id(memberJoinForm.getMember_id());
	        member.setPassword(memberJoinForm.getPassword());
	        member.setName(memberJoinForm.getName());
	        member.setBirth(memberJoinForm.getBirth());
	        member.setEmail(memberJoinForm.getEmail());
	 
	        
	        // 변환된 Member 엔티티를 저장
	        memberRepository.save(member);
	    }
	
	public Member findMemberById(String member_id) {
		Optional<Member> member = memberRepository.findById(member_id);
		return member.orElse(null);
	}
	 @Transactional
	    public boolean deleteMember(String memberId) {
	        Optional<Member> member = memberRepository.findById(memberId);
	        if (member.isPresent()) {
	            memberRepository.delete(member.get());
	            return true;
	        }
	        return false;
	    }
	 
	  @Transactional
	    public Member updateMember(String memberId, Member updatedMember) {
	        Optional<Member> existingMember = memberRepository.findById(memberId);
	        if (existingMember.isPresent()) {
	            Member memberToUpdate = existingMember.get();
	            
	            // 업데이트할 필드들을 설정
	            memberToUpdate.setPassword(updatedMember.getPassword());
	            memberToUpdate.setName(updatedMember.getName());
	            memberToUpdate.setBirth(updatedMember.getBirth());
	            memberToUpdate.setEmail(updatedMember.getEmail());
	            memberToUpdate.setEco_point(updatedMember.getEco_point());
	            
	            
	            return memberRepository.save(memberToUpdate);
	        }
	        return null;
	}
	  
	  public boolean login(String member_id, String password) {
		    Optional<Member> memberOpt = memberRepository.findById(member_id);
		    if (memberOpt.isPresent()) {
		        Member member = memberOpt.get();
		        if (member.getPassword().equals(password)) {
		            // 비밀번호 일치
		            return true;
		        }
		    }
		    return false;
		}
	  
}