package com.example.board.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
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
	    public boolean deleteMember(String member_id) {
	        Optional<Member> member = memberRepository.findById(member_id);
	        if (member.isPresent()) {
	            memberRepository.delete(member.get());
	            return true;
	        }
	        return false;
	    }
	 
	 @Transactional
	 public Member updateMemberInfo(String member_id, MemberUpdateRequest updateRequest) {
		    Optional<Member> existingMemberOpt = memberRepository.findById(member_id);
		    if (existingMemberOpt.isPresent()) {
		        Member memberToUpdate = existingMemberOpt.get();
		        
		        // 업데이트할 필드들을 설정
		        if (updateRequest.getName() != null) memberToUpdate.setName(updateRequest.getName());
		        if (updateRequest.getBirth() != null) memberToUpdate.setBirth(updateRequest.getBirth());
		        if (updateRequest.getEmail() != null) memberToUpdate.setEmail(updateRequest.getEmail());
		        if (updateRequest.getNewPassword() != null) memberToUpdate.setPassword(updateRequest.getNewPassword());

		        memberRepository.save(memberToUpdate);
		        return memberToUpdate; // 성공적으로 업데이트된 Member 객체 반환
		    }
		    return null; // 업데이트 실패
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
	  
	  /**
	     * 회원 ID를 기반으로 회원 정보를 조회하여 DTO로 변환합니다.
	     *
	     * @param memberId 조회할 회원의 ID
	     * @return 회원 정보를 담고 있는 MemberProfileDto 객체
	     */
	    public MemberProfileDto getMemberProfile(String member_id) {
	        // 회원 정보를 데이터베이스에서 조회
	        Member member = memberRepository.findById(member_id)
	                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

	        // Member 엔티티를 DTO로 변환하여 반환
	        return new MemberProfileDto(
	            member.getMember_id(),
	            member.getName(),
	            member.getBirth(),
	            member.getEmail(),
	            member.getEco_point()
	        );
	    }
	    
	    //비밀번호 수정
	    @Transactional
	    public boolean updatePassword(String member_id, String currentPassword, String newPassword, String confirmNewPassword) {
	        Optional<Member> memberOpt = memberRepository.findById(member_id);
	        if (memberOpt.isPresent()) {
	            Member member = memberOpt.get();

	            if (!member.getPassword().equals(currentPassword)) {
	                return false; // 현재 비밀번호가 일치하지 않음
	            }
	            if (!newPassword.equals(confirmNewPassword)) {
	                return false; // 새로운 비밀번호와 확인 비밀번호가 일치하지 않음
	            }

	            member.setPassword(newPassword);
	            memberRepository.save(member);
	            return true;
	        }
	        return false;
	    }

	  
}