package com.example.board.controller;

import java.util.HashMap;
import java.util.Map;

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
import com.example.board.util.JwtTokenUtil;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/members")

public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;


    public MemberController(MemberService memberService, JwtTokenUtil jwtTokenUtil) {
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    // 유저 등록
    @PostMapping("/register")
    public ResponseEntity<MemberJoinForm> registerMember(@RequestBody MemberJoinForm memberJoinForm) {
        memberService.saveMember(memberJoinForm);
        return ResponseEntity.ok(memberJoinForm);
    }
    
    
    // 로그인
    @PostMapping("/login")
    
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        
        try {
            // 사용자 인증
            boolean isAuthenticated = memberService.login(loginForm.getMember_id(), loginForm.getPassword());
            
            if (isAuthenticated) {
                // 사용자 세부 정보 로드
                UserDetails userDetails = memberService.loadUserByUsername(loginForm.getMember_id());
                
                // JWT 토큰 생성
                String token = jwtTokenUtil.generateToken(userDetails);
                
                response.put("success", true);
                response.put("message", "로그인 성공");
                response.put("token", token); // JWT 토큰 반환
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "로그인 실패");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 유저 1명 조회
    @GetMapping("/{member_id}")
    public ResponseEntity<Member> getMember(@PathVariable("member_id") String member_id, @RequestHeader("Authorization") String token) {
        if (jwtTokenUtil.validateToken(token, memberService.loadUserByUsername(member_id))) {
            Member member = memberService.findMemberById(member_id);
            if (member != null) {
                return ResponseEntity.ok(member);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // 회원 탈퇴
    @DeleteMapping("/{member_id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable("member_id") String member_id,
            @RequestParam("password") String password,
            @RequestHeader("Authorization") String token) {

        if (jwtTokenUtil.validateToken(token, memberService.loadUserByUsername(member_id))) {
            Member member = memberService.findMemberById(member_id);
            if (member == null) {
                return ResponseEntity.notFound().build();
            }

            boolean isPasswordValid = memberService.validatePassword(member_id, password);
            if (!isPasswordValid) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            boolean isDeleted = memberService.deleteMember(member_id);
            if (isDeleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // 유저 정보 수정
    @PutMapping("/{member_id}")
    public ResponseEntity<Member> updateMemberInfo(
            @PathVariable("member_id") String member_id,
            @RequestBody MemberUpdateRequest updateRequest,
            @RequestHeader("Authorization") String token) {
        if (jwtTokenUtil.validateToken(token, memberService.loadUserByUsername(member_id))) {
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
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // 비밀번호 변경 (토큰 기반 인증 필요)
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable String member_id,
            @RequestBody PasswordUpdateRequest request,
            @RequestHeader("Authorization") String token) {
        if (jwtTokenUtil.validateToken(token, memberService.loadUserByUsername(member_id))) {
            try {
                boolean isUpdated = memberService.updatePassword(
                        member_id,
                        request.getCurrentPassword(),
                        request.getNewPassword(),
                        request.getConfirmNewPassword()
                );

                if (isUpdated) {
                    return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "현재 비밀번호가 올바르지 않거나 비밀번호 확인이 일치하지 않습니다."));
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "비밀번호 변경 실패"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    
    // 마이페이지 (토큰 기반 인증 필요)
    @GetMapping("/mypage")
    public ResponseEntity<MemberProfileDto> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // "Bearer " 제거
                String member_id = jwtTokenUtil.extractUsername(token);
                log.info("Extracted member_id from token: {}", member_id);
                
                if (jwtTokenUtil.validateToken(token, memberService.loadUserByUsername(member_id))) {
                    log.info("Token is valid. Fetching profile information.");
                    MemberProfileDto profile = memberService.getMemberProfile(member_id);
                    if (profile != null) {
                        return ResponseEntity.ok(profile);
                    } else {
                        log.warn("Profile not found for member_id: {}", member_id);
                        return ResponseEntity.notFound().build();
                    }
                } else {
                    log.warn("Token validation failed for member_id: {}", member_id);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                log.warn("Authorization header is missing or invalid.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            log.error("Error processing mypage request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    
    // 로그아웃 (클라이언트 측에서 토큰을 삭제하는 방식으로 처리)
    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃 성공"));
    }
}
