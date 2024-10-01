package com.example.board.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.dto.EcoProductPurchaseDTO;
import com.example.board.dto.MemberProfileDto;
import com.example.board.dto.MemberUpdateRequest;
import com.example.board.dto.PurchaseDTO;
import com.example.board.dto.SalesDTO;
import com.example.board.model.ecoProduct.EcoProductPurchase;
import com.example.board.model.member.Member;
import com.example.board.model.member.MemberJoinForm;
import com.example.board.model.product.Product;
import com.example.board.model.product.Purchase;
import com.example.board.repository.EcoProductPurchaseRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.PurchaseRepository;
import com.example.board.util.PasswordUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final ProductRepository productRepository;
	private final PurchaseRepository purchaseRepository;
	private final EcoProductPurchaseRepository ecoPurchaseRepository;

	@Value("${profile.images.upload-dir:/path/to/profile-images/}")
	private String uploadDir; // @Value 어노테이션을 사용하여 프로퍼티 값 주입

//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder(); // BCryptPasswordEncoder 인스턴스 생성
//    }

	@Transactional
	public void saveMember(MemberJoinForm memberJoinForm) {
		// member_id 중복 확인
		if (memberRepository.existsByMember_id(memberJoinForm.getMember_id())) {
			throw new IllegalArgumentException("이미 존재하는 회원 ID입니다.");
		}

		// email 중복 확인
		if (memberRepository.existsByEmail(memberJoinForm.getEmail())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		// name 중복 확인
		if (memberRepository.existsByName(memberJoinForm.getName())) {
			throw new IllegalArgumentException("이미 존재하는 이름입니다.");
		}

		// 중복 검사를 모두 통과한 경우에만 회원 저장

		Member member = new Member();
		member.setMember_id(memberJoinForm.getMember_id());
		member.setPassword(PasswordUtils.hashPassword(memberJoinForm.getPassword())); // 비밀번호 해시화
		member.setName(memberJoinForm.getName());
		member.setEmail(memberJoinForm.getEmail());
		member.setArea(memberJoinForm.getArea());

		memberRepository.save(member);
	}

	public void saveMember(Member member) {
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

	public Optional<Member> findById(String memberId) {
		return memberRepository.findById(memberId);
	}

	@Transactional
	public Member updateMemberInfo(String member_id, MemberUpdateRequest updateRequest) {

		Optional<Member> existingMemberOpt = memberRepository.findById(member_id);
		if (existingMemberOpt.isPresent()) {
			Member memberToUpdate = existingMemberOpt.get();

			// 업데이트할 필드들을 설정
			if (updateRequest.getName() != null)
				memberToUpdate.setName(updateRequest.getName());
			if (updateRequest.getEmail() != null)
				memberToUpdate.setEmail(updateRequest.getEmail());
//			if (updateRequest.getNewPassword() != null)
//				memberToUpdate.setPassword(passwordEncoder.encode(updateRequest.getNewPassword())); // 비밀번호 해시화
			if (updateRequest.getArea() != null)
				memberToUpdate.setArea(updateRequest.getArea());

			return memberRepository.save(memberToUpdate); // 성공적으로 업데이트된 Member 객체 반환
		}
		return null; // 업데이트 실패
	}

	public void save(Member member) {
		memberRepository.save(member);
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

	// 프로필 이미지 업로드 로직
	public String uploadProfileImage(String memberId, MultipartFile file) throws IOException {
		// 업로드 디렉토리 확인 및 생성
		File uploadDir = new File(this.uploadDir);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}

		// 파일 이름 생성
		String fileName = memberId + "-profile-image.jpg";
		File destinationFile = new File(uploadDir, fileName);
		file.transferTo(destinationFile); // 파일 저장

		// 회원 조회
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 기존 프로필 이미지 URL이 있는 경우 삭제 (URL을 빈 문자열로 변경)
		if (member.getProfileImageUrl() != null) {
			// 기존 URL 처리 (예: 파일 삭제)
			File oldFile = new File(member.getProfileImageUrl());
			if (oldFile.exists()) {
				oldFile.delete(); // 기존 이미지 파일 삭제
			}
		}

		// Member의 프로필 이미지 URL 설정
		member.setProfileImageUrl(destinationFile.getAbsolutePath());
		memberRepository.save(member); // Member 저장

		return fileName;
	}

	@Transactional
	public void saveProfileImage(String memberId, String imageUrl) {
		// Member를 DB에서 찾아서 해당 프로필 이미지 URL 저장
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 프로필 이미지 URL 설정
		member.setProfileImageUrl(imageUrl); // 이미지 경로를 설정

		memberRepository.save(member); // Member 저장
	}

	@Transactional
	public MemberProfileDto getMemberProfile(String memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 프로필 이미지 경로를 문자열로 가져옵니다.
		String profileImagePath = member.getProfileImageUrl();

		return new MemberProfileDto(member.getMember_id(), member.getName(), member.getEmail(), member.getEco_point(),
				profileImagePath, member.getArea());
	}

	@Transactional
	public boolean updatePassword(String member_id, String currentPassword, String newPassword,
			String confirmNewPassword) {
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

	// 판매 이력 가져오기
	@Transactional
	public List<SalesDTO> getSalesHistory(String memberId) {
		// 멤버 존재 여부 확인
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 판매 이력 가져오기
		List<Product> salesHistory = productRepository.findByMember(member);

		return salesHistory.stream().map(product -> {
			// SalesDTO로 변환
			SalesDTO salesDTO = new SalesDTO();
			salesDTO.setId(product.getId()); // 제품 ID
			salesDTO.setTitle(product.getTitle()); // 제품 제목
			salesDTO.setPrice(product.getPrice()); // 가격
			salesDTO.setContents(product.getContents()); // 내용
			salesDTO.setCreatedTime(product.getCreated_time()); // 생성 시간

			// 판매자 정보 추가
			salesDTO.setSellerName(product.getMember().getName()); // 판매자의 닉네임
			salesDTO.setSellerProfileImage(product.getMember().getProfileImageUrl()); // 판매자의 프로필 이미지

			// 거래 상태 추가 (null 체크)
			if (product.getStatus() != null) {
				salesDTO.setStatus(product.getStatus().name()); // Enum을 문자열로 변환하여 설정
			} else {
				salesDTO.setStatus("Unknown"); // 상태가 null일 경우 기본값 설정
			}

			return salesDTO;
		}).collect(Collectors.toList());
	}

	// 구매 이력 가져오기
	@Transactional
	public List<PurchaseDTO> getPurchaseHistory(String memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

		// 구매 이력 가져오기
		List<Purchase> purchases = purchaseRepository.findByBuyerId(memberId);

		// Purchase 엔티티를 PurchaseDTO로 변환하여 반환
		return purchases.stream().map(PurchaseDTO::fromEntity) // fromEntity 메서드를 사용하여 변환
				.collect(Collectors.toList());
	}

	@Transactional
	public List<EcoProductPurchaseDTO> getEcoPurchaseHistory(String memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

	    List<EcoProductPurchase> purchases = ecoPurchaseRepository.findByBuyerId(memberId);
		 // purchases의 내용을 출력
	    if (purchases.isEmpty()) {
	        System.out.println("구매 기록이 없습니다.");  // 구매 기록이 없을 경우
	    } else {
	        System.out.println("구매 기록: " + purchases);  // 구매 기록을 출력
	    }

	    return purchases.stream()
	            .map(EcoProductPurchaseDTO::fromEntity)
	            .collect(Collectors.toList());
	}

	public Member findByName(String name) {
		return memberRepository.findByName(name)
				.orElseThrow(() -> new IllegalArgumentException("Member not found with name: " + name));
	}

}