package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
import com.example.board.model.member.LoginForm;
import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
import com.example.board.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/members")


public class MemberController {
	
	private final MemberService memberService;
	
	@Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	//유저 등록
	@PostMapping("/register")
	public ResponseEntity<MemberJoinForm> registerMember(@RequestBody MemberJoinForm memberJoinForm) {
		memberService.saveMember(memberJoinForm);
		return ResponseEntity.ok(memberJoinForm);
	}
	
	//유저 1명 조회
	@GetMapping("/{member_id}")
	public ResponseEntity<Member> getMember(@PathVariable("member_id") String member_id) {
		Member member = memberService.findMemberById(member_id);
		
		if (member != null) {
			return ResponseEntity.ok(member);
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	
	//유저 탈퇴
	@DeleteMapping("/{member_id}")
	    public ResponseEntity<Void> deleteMember(@PathVariable("member_id") String member_id) {
	        boolean isDeleted = memberService.deleteMember(member_id);
	        if (isDeleted) {
	            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	}
	
	
	//유저 정보 수정
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

	
	
	
	// 로그인
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginForm loginForm, HttpSession session) {
	    try {
	        boolean isAuthenticated = memberService.login(loginForm.getMember_id(), loginForm.getPassword());
	        if (isAuthenticated) {
	            // 로그인 성공 시 세션에 사용자 ID 저장
	            session.setAttribute("loggedInUser", loginForm.getMember_id());

	            // 로그인한 사용자 정보를 가져오기
	            Member member = memberService.findMemberById(loginForm.getMember_id());

	            // Member 객체의 name만 반환
	            if (member != null) {
	                return ResponseEntity.ok(member.getName());
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원 정보를 찾을 수 없습니다.");
	            }
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인에 실패했습니다.");
	        }
	    } catch (Exception e) {
	        // 예외 로그 출력
	        System.err.println("Exception occurred: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
	    }
	}

	
	//마이페이지
	@GetMapping("/mypage")
	public ResponseEntity<MemberProfileDto> getMemberProfile(@RequestParam("member_id") String member_id) {
	    MemberProfileDto profile = memberService.getMemberProfile(member_id);
	    if (profile != null) {
	        return ResponseEntity.ok(profile);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	//로그아웃 처리
		@GetMapping("/logout")
		public String logout(HttpServletResponse response,
							HttpServletRequest request) {
			
			//세션으로 로그아웃 처리
			HttpSession session = request.getSession();
			
			//1.같은 이름으로 덮어 씌우기
			session.setAttribute("loginMember", null);
			
			//2.일괄적으로 세션값을 리셋
			session.invalidate();
			
			return "redirect:/";
			
			
		}
		

		}
