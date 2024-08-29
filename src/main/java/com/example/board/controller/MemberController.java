package com.example.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
import com.example.board.dto.PasswordUpdateRequest;
import com.example.board.model.member.LoginForm;
import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
import com.example.board.service.MemberService;
import com.example.board.util.JwtTokenProvider;
import com.example.board.util.PasswordUtils;



import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;

    }

    // 유저 등록
    @PostMapping("/register")
    public ResponseEntity<MemberJoinForm> registerMember(@RequestBody MemberJoinForm memberJoinForm) {
        memberService.saveMember(memberJoinForm);
        return ResponseEntity.ok(memberJoinForm);
    }
    
    
    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) {
        Map<String, Object> response = new HashMap<>();
        
        
        try {
            // 사용자 인증
            boolean isAuthenticated = memberService.login(loginForm.getMember_id(), loginForm.getPassword());
            if (isAuthenticated) {
                Member member = memberService.findMemberById(loginForm.getMember_id());
                String token = jwtTokenProvider.createToken(member.getMember_id());

                response.put("success", true);
                response.put("message", "로그인 성공");
                response.put("token", token);
                response.put("name", member.getName());

                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "로그인 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Login error", e);

            response.put("success", false);
            response.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    //마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<MemberProfileDto> getMemberProfile(@RequestHeader("Authorization") String token) {
        String memberId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        MemberProfileDto profile = memberService.getMemberProfile(memberId);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
            else {
                return ResponseEntity.notFound().build();
            }
        }


    // 유저 1명 조회
    @GetMapping("/{member_id}")
    public ResponseEntity<Member> getMember(@PathVariable("member_id") String member_id) {
        Member member = memberService.findMemberById(member_id);
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    // 회원 탈퇴
    @DeleteMapping("/{member_id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable("member_id") String member_id,
            @RequestParam("password") String password)
             {

            	// 회원 정보를 조회
                Member member = memberService.findMemberById(member_id);
                if (member == null) {
                    return ResponseEntity.notFound().build();
                }

                // 비밀번호 검증
                boolean isPasswordValid = PasswordUtils.validatePassword(password, member.getPassword());
                if (!isPasswordValid) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 비밀번호가 일치하지 않으면 403 Forbidden 반환
                }

                // 회원 탈퇴 처리
                boolean isDeleted = memberService.deleteMember(member_id);
                if (isDeleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            }

    // 유저 정보 수정
    @PutMapping("/{member_id}")
    public ResponseEntity<Member> updateMemberInfo(
            @PathVariable("member_id") String member_id,
            @RequestBody MemberUpdateRequest updateRequest) {
        try {
            Member updatedMember = memberService.updateMemberInfo(member_id, updateRequest);
            if (updatedMember != null) {
                return ResponseEntity.ok(updatedMember);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
  

   
    // 로그아웃 (JWT 기반에서는 특별한 로그아웃 처리가 필요하지 않음)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT 기반에서는 서버에서 세션을 관리하지 않으므로 로그아웃 시 특별한 처리를 할 필요가 없습니다.
        // 클라이언트가 토큰을 폐기하거나, 서버에서 토큰을 블랙리스트에 추가하는 등의 방법을 사용할 수 있습니다.
        return ResponseEntity.noContent().build();

    }
}