package com.example.board.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.board.dto.EcoProductDTO;
import com.example.board.dto.EcoProductPurchaseDTO;
import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
import com.example.board.dto.PasswordUpdateRequest;
import com.example.board.dto.PurchaseDTO;
import com.example.board.dto.SalesDTO;
import com.example.board.model.ecoProduct.EcoProduct;
import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.member.LoginForm;
import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
import com.example.board.model.member.ProfileImageRequest;
import com.example.board.repository.MemberRepository;
import com.example.board.service.EcoProductService;
import com.example.board.service.MemberService;
import com.example.board.util.JwtTokenProvider;
import com.example.board.util.PasswordUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {
	private final MemberService memberService;
	private final EcoProductService ecoProductService;
	private final JwtTokenProvider jwtTokenProvider;

	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider,
			MemberRepository memberRepository, EcoProductService ecoProductService) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.memberRepository = memberRepository;
		this.ecoProductService = ecoProductService;
	}

	@Autowired
	private final MemberRepository memberRepository;

	public Optional<Member> findById(String memberId) {
		return memberRepository.findById(memberId);
	}

	// 유저 등록
	@PostMapping("/register")
	public ResponseEntity<MemberJoinForm> registerMember(@Valid @RequestBody MemberJoinForm memberJoinForm) {

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
				response.put("member_id", member.getMember_id()); // member_id를 추가로 반환
				response.put("profileImage", member.getProfileImageUrl());

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

	// 프로필 이미지 업로드 API
	@PostMapping("/profile/upload")
	public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestBody ProfileImageRequest profileImageRequest,
			@RequestHeader("Authorization") String token) {

		log.info("profileImageRequest: {}", profileImageRequest);

		try {
			// JWT 토큰에서 사용자 ID 추출
			String memberId = jwtTokenProvider
					.getMemberIdFromToken(JwtTokenProvider.extractAndValidateToken(token, jwtTokenProvider));

			// 요청에서 받은 memberId와 비교 (선택 사항)
//			if (!memberId.equals(profileImageRequest.getMemberId())) {
//				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "권한이 없습니다."));
//			}

			// 프로필 이미지 URL 처리 (예: 저장 또는 업데이트 로직)
			String imageUrl = profileImageRequest.getUrl();
			// 여기에 imageUrl을 파일로 저장하는 로직을 추가하세요

			memberService.saveProfileImage(memberId, imageUrl);

			Map<String, String> response = new HashMap<>();
			response.put("message", "프로필 이미지가 성공적으로 업로드되었습니다.");
			response.put("imageUrl", imageUrl); // 필요한 경우 이미지 URL 반환

			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			log.error("프로필 이미지 업로드 실패", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "업로드 중 오류 발생"));
		}
	}

//	@GetMapping("/profile/images/{memberId}")
//	public ResponseEntity<Map<String, String>> getProfileImage(@PathVariable("memberId") String memberId) {
//		try {
//			// memberId로 Member 객체 조회
//			Member member = memberRepository.findById(memberId)
//					.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
//
//			// Member로 프로필 이미지 정보를 데이터베이스에서 조회
//			String imageUrl = member.getProfileImageUrl(); // Assume this returns a String URL
//
//			if (imageUrl == null || imageUrl.isEmpty()) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "프로필 이미지가 존재하지 않습니다."));
//			}
//
//			// 이미지 URL을 Map으로 반환
//			Map<String, String> response = new HashMap<>();
//			response.put("message", "프로필 이미지 조회 성공");
//			response.put("imageUrl", imageUrl);
//
//			return ResponseEntity.ok(response);
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
//		} catch (Exception e) {
//			log.error("프로필 이미지 조회 실패", e);
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이미지 조회 중 오류 발생"));
//		}
//	}

	// 마이페이지
	@GetMapping("/mypage")
	public ResponseEntity<MemberProfileDto> getMemberProfile(@RequestHeader("Authorization") String token) {
		String memberId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
		if (memberId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		MemberProfileDto profile = memberService.getMemberProfile(memberId);
		if (profile != null) {
			return ResponseEntity.ok(profile);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 유저 1명 조회
	@GetMapping("/{member_id}")
	public ResponseEntity<MemberProfileDto> getMember(@PathVariable("member_id") String member_id) {
		// MemberService에서 getMemberProfile 메서드를 호출
		MemberProfileDto memberProfileDTO = memberService.getMemberProfile(member_id);

		// DTO가 null이 아닌 경우 OK 응답, 그렇지 않으면 404 응답
		if (memberProfileDTO != null) {
			return ResponseEntity.ok(memberProfileDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 회원 탈퇴
	@DeleteMapping("/{member_id}")
	public ResponseEntity<Void> deleteMember(@PathVariable("member_id") String member_id,
			@RequestParam("password") String password) {

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
	public ResponseEntity<Void> updateMemberInfo(@PathVariable("member_id") String member_id,
			@RequestBody MemberUpdateRequest updateRequest) {
		try {
			// 기존 회원 정보 불러오기
			Optional<Member> existingMemberOpt = memberService.findById(member_id);
			log.info("existingMemberOpt: {}", existingMemberOpt);
			if (!existingMemberOpt.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}

			Member existingMember = existingMemberOpt.get();

			// 정보 업데이트
			existingMember.setName(updateRequest.getName());
			existingMember.setEmail(updateRequest.getEmail());
			existingMember.setArea(updateRequest.getArea());

			// 업데이트된 회원 정보 저장 (반환값 없이 저장)
			memberService.save(existingMember);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// 비밀번호 수정
	@PutMapping("/password/update")
	public ResponseEntity<Map<String, String>> updatePassword(@RequestHeader("Authorization") String token,
			@RequestBody PasswordUpdateRequest passwordUpdateRequest) {

		try {
			// JWT 토큰에서 사용자 ID 추출
			String memberId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

			// 비밀번호 변경 시도
			boolean success = memberService.updatePassword(memberId, passwordUpdateRequest.getCurrentPassword(),
					passwordUpdateRequest.getNewPassword(), passwordUpdateRequest.getConfirmNewPassword());

			if (success) {
				return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("message", "비밀번호 변경에 실패했습니다. 현재 비밀번호 또는 새로운 비밀번호 확인을 해주세요."));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "서버 오류로 비밀번호 변경에 실패했습니다."));
		}
	}

	// 판매 내역 조회
	@GetMapping("mypage/sales")
	public ResponseEntity<List<SalesDTO>> getSalesHistory(@RequestHeader("Authorization") String authorizationHeader) {

		// Authorization 헤더에서 'Bearer' 접두어 제거
		String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7)
				: authorizationHeader;

		// 토큰 검증 및 멤버 ID 확인
		if (!jwtTokenProvider.validateToken(token)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or expired token");
		}

		String tokenMemberId = jwtTokenProvider.getUserIdFromToken(token);

		// 판매 내역 조회
		List<SalesDTO> salesHistoryDTO = memberService.getSalesHistory(tokenMemberId);

		return ResponseEntity.ok(salesHistoryDTO);
	}

	// 구매 내역 조회
	@GetMapping("mypage/purchases")
	public ResponseEntity<List<PurchaseDTO>> getPurchaseHistory(
			@RequestHeader("Authorization") String authorizationHeader) {

		// Authorization 헤더에서 'Bearer' 접두어 제거
		String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7)
				: authorizationHeader;

		// 토큰 검증 및 멤버 ID 확인
		if (!jwtTokenProvider.validateToken(token)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or expired token");
		}

		String tokenMemberId = jwtTokenProvider.getUserIdFromToken(token);

		// 구매 내역 조회
		List<PurchaseDTO> purchaseHistory = memberService.getPurchaseHistory(tokenMemberId);

		return ResponseEntity.ok(purchaseHistory);
	}

	@GetMapping("mypage/ecoproducts")
	public ResponseEntity<List<EcoProductPurchaseDTO>> getEcoProductPurchaseHistory(
			@RequestHeader("Authorization") String authorizationHeader) {

		// Authorization 헤더에서 'Bearer' 접두어 제거
		String token = authorizationHeader.startsWith("Bearer ") ? authorizationHeader.substring(7)
				: authorizationHeader;

		// 토큰 검증 및 멤버 ID 확인
		if (!jwtTokenProvider.validateToken(token)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or expired token");
		}

		String tokenMemberId = jwtTokenProvider.getUserIdFromToken(token);

		// EcoProduct 구매 내역 조회
		List<EcoProductPurchaseDTO> ecoPurchaseHistory = memberService.getEcoPurchaseHistory(tokenMemberId);

		return ResponseEntity.ok(ecoPurchaseHistory);
	}

	// 로그아웃 (JWT 기반에서는 특별한 로그아웃 처리가 필요하지 않음)
	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		// JWT 기반에서는 서버에서 세션을 관리하지 않으므로 로그아웃 시 특별한 처리를 할 필요가 없습니다.
		// 클라이언트가 토큰을 폐기하거나, 서버에서 토큰을 블랙리스트에 추가하는 등의 방법을 사용할 수 있습니다.
		return ResponseEntity.noContent().build();

	}

	// 에코구매내역
	@GetMapping("/ecoPurchase")
	public ResponseEntity<List<EcoProductDTO>> list(
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "12") int size) {

		Pageable pageable = PageRequest.of(page, size);

		List<EcoProduct> ecoProductList = (searchText != null && !searchText.isEmpty())
				? ecoProductService.findSearch(searchText, pageable).getContent() // Page에서 내용만 가져옴
				: ecoProductService.findAll(pageable).getContent(); // Page에서 내용만 가져옴

		List<EcoProductDTO> ecoProductDTOs = ecoProductList.stream().map(EcoProductDTO::fromEntity)
				.collect(Collectors.toList());

		return ResponseEntity.ok(ecoProductDTOs);
	}
}