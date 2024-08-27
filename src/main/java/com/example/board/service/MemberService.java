package com.example.board.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
import com.example.board.repository.MemberRepository;


@Service
public class MemberService {

	private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // BCryptPasswordEncoder 인스턴스 생성
    }
    @Transactional
    public void saveMember(MemberJoinForm memberJoinForm) {
        Member member = new Member();
        member.setMember_id(memberJoinForm.getMember_id());
        member.setPassword(passwordEncoder.encode(memberJoinForm.getPassword())); // 올바른 비밀번호 인코딩

        member.setName(memberJoinForm.getName());
        member.setBirth(memberJoinForm.getBirth());
        member.setEmail(memberJoinForm.getEmail());

        memberRepository.save(member);
    }

    public Member findMemberById(String member_id) {
        return memberRepository.findById(member_id).orElse(null);
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
            if (updateRequest.getNewPassword() != null) 
            	 memberToUpdate.setPassword(passwordEncoder.encode(updateRequest.getNewPassword())); // 비밀번호 해시화

            return memberRepository.save(memberToUpdate); // 성공적으로 업데이트된 Member 객체 반환
        }
        return null; // 업데이트 실패
    }
    
    //비밀번호 업데이트
    @Transactional
    public boolean updatePassword(String member_id, String currentPassword, String newPassword, String confirmNewPassword) {
        Optional<Member> memberOpt = memberRepository.findById(member_id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // 비밀번호 검증
            if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
                return false; // 현재 비밀번호가 일치하지 않음
            }
            if (!newPassword.equals(confirmNewPassword)) {
                return false; // 새로운 비밀번호와 확인 비밀번호가 일치하지 않음
            }

            member.setPassword(passwordEncoder.encode(newPassword)); // 비밀번호 해시화
            memberRepository.save(member);
            return true;
        }
        return false;
    }


    public boolean login(String member_id, String password) {
    	  Member member = findMemberById(member_id);
          if (member == null) {
        	  System.out.println("Member not found for ID: " + member_id);
              return false;
          }
          
          boolean isPasswordValid = passwordEncoder.matches(password, member.getPassword());
          if (!isPasswordValid) {
              System.out.println("Invalid password for member ID: " + member_id);
          }

          return isPasswordValid;
    }
          
          

    
    public MemberProfileDto getMemberProfile(String member_id) {
        Optional<Member> memberOptional = memberRepository.findById(member_id);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return new MemberProfileDto(
                member.getMember_id(),
                member.getName(),
                member.getBirth(),
                member.getEmail(),
                member.getEco_point()
            );
        } else {
            return null; // 회원이 존재하지 않는 경우 null 반환
        }
    }
    
    public boolean validatePassword(String member_id, String password) {
        Member member = findMemberById(member_id);
        return member != null && passwordEncoder.matches(password, member.getPassword());
    }


    public UserDetails loadUserByUsername(String member_id) throws UsernameNotFoundException {
    	Member member = memberRepository.findById(member_id)
    			.orElseThrow(() -> new UsernameNotFoundException("Member not found with ID: " + member_id));
    	return org.springframework.security.core.userdetails.User.builder()
    			.username(member.getMember_id())
    			.password(member.getPassword())
    			.roles("USER")
    			.build();
    }
}
