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
import com.example.board.util.PasswordUtils;


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
        member.setPassword(PasswordUtils.hashPassword(memberJoinForm.getPassword())); // 비밀번호 해시화
        member.setName(memberJoinForm.getName());
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
            if (updateRequest.getEmail() != null) memberToUpdate.setEmail(updateRequest.getEmail());
            if (updateRequest.getNewPassword() != null) 
            	 memberToUpdate.setPassword(passwordEncoder.encode(updateRequest.getNewPassword())); // 비밀번호 해시화

            return memberRepository.save(memberToUpdate); // 성공적으로 업데이트된 Member 객체 반환
        }
        return null; // 업데이트 실패
    }


    public boolean login(String member_id, String password) {
        Optional<Member> memberOpt = memberRepository.findById(member_id);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            boolean isPasswordValid = PasswordUtils.validatePassword(password, member.getPassword());
            if (isPasswordValid) {
                return true; // 비밀번호 일치
            } else {
                // 비밀번호 불일치 로그 추가
                System.out.println("비밀번호 불일치: member_id = " + member_id);
            }
        } else {
            // 회원이 존재하지 않음 로그 추가
            System.out.println("회원 존재하지 않음: member_id = " + member_id);
        }
        return false; // 로그인 실패
    }
    
    
    public MemberProfileDto getMemberProfile(String member_id) {
        Member member = memberRepository.findById(member_id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return new MemberProfileDto(
            member.getMember_id(),
            member.getName(),
            member.getEmail(),
            member.getEco_point()
        );
    }


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
        return false; // 회원이 존재하지 않음
    }
}
