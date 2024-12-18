package com.example.board.util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.board.model.member.Area;
import com.example.board.model.member.Member;
import com.example.board.model.product.Category;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.service.MemberService;

import java.time.temporal.ChronoUnit;

@Component
public class MacroUtil {
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private MemberService memberService;
	
	private final String[] names = { "홍길동", "김개똥", "이영수", "이지민", "최강섭", "허지수", "김익명", "여진수", "유진아", "Judy" };
	private final String[] emails = { "hong@example.com", "kimg@example.com", "lee@example.com", "jimin@example.com",
			"kang@example.com", "jisu@example.com", "ik@example.com", "jinsu@example.com", "jina@example.com",
			"judy@example.com" };

	// 이미지 URL 배열 정의
	private final String[] imageUrls = {
			"https://firebasestorage.googleapis.com/v0/b/ecotrade-530ef.appspot.com/o/images%2F20241013_104605.jpg?alt=media&token=64715f1c-c5b2-4fd0-b3e5-026632e3f2c2"
				};

	private Random random = new Random();

	public void registerMembersAndProducts() {
		// 10명의 회원 등록
		for (int i = 0; i < 10; i++) {
			Member member = new Member();
			member.setMember_id("aaaa" + (i + 1));
			member.setName(names[i % names.length]);
			member.setEmail(emails[i % emails.length]);
			member.setPassword(PasswordUtils.hashPassword("aaaa" + (i + 1)));
			member.setEco_point(0L); // 초기 포인트 설정
			member.setArea(Area.values()[random.nextInt(Area.values().length)]); // 랜덤 지역 설정

			memberRepository.save(member);
		}

		// 30개의 랜덤 상품 등록
		for (int i = 0; i < 30; i++) {
			Product product = new Product();
			product.setTitle("상품 " + (i + 1));
			product.setContents("여기에 상품 설명을 적어주세요 " + (i + 1));
			product.setPrice(random.nextLong(1000, 100000)); // 가격 범위 설정
			product.setCreatedTime(getRandomPastDateTime()); // 랜덤 시간으로 설정
			product.setMember(memberRepository.findById("aaaa" + (random.nextInt(10) + 1)).orElse(null)); // 랜덤 회원 설정
			product.setCategory(Category.values()[random.nextInt(Category.values().length)]); // 랜덤 카테고리 설정

			// 랜덤 이미지 선택
			int imageCount = random.nextInt(2) + 1; // 1개 또는 2개 이미지를 선택
			List<Image> images = new ArrayList<>();
			for (int j = 0; j < imageCount; j++) {
				Image image = new Image();
				image.setUrl(imageUrls[random.nextInt(imageUrls.length)]); // 랜덤 이미지 URL 선택
				image.setProduct(product); // product 설정
				images.add(image);
			}
			product.setProductImages(images); // 이미지 리스트에 추가

			productRepository.save(product);
		}
	}

	// 랜덤 날짜 생성 메서드
	private LocalDateTime getRandomPastDateTime() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime twoYearsAgo = now.minusYears(2);

		long secondsBetween = ChronoUnit.SECONDS.between(twoYearsAgo, now);
		long randomSeconds = random.nextLong(secondsBetween);

		return twoYearsAgo.plusSeconds(randomSeconds);
	}

	// admin 계정 생성 메서드
	public void registerAdmin() {
		Member admin = new Member();
		admin.setMember_id("admin");
		admin.setName("admin");
		admin.setEmail("admin@example.com");
		admin.setPassword(PasswordUtils.hashPassword("admin")); // 비밀번호 해싱
		admin.setEco_point(10000L); // 초기 포인트를 임의로 설정, 필요에 따라 조정 가능
		admin.setArea(Area.SEOUL); // 지역을 지정, 필요에 따라 변경 가능

		// 프로필 이미지 설정
		admin.setProfileImageUrl(
				"https://firebasestorage.googleapis.com/v0/b/ecotrade-530ef.appspot.com/o/images%2Fadministrator_17328413.png?alt=media&token=3d5146a3-d1f8-4118-af6a-91604cd2dcfd");

		memberRepository.save(admin);
		System.out.println("Admin account created successfully!");
	}

	public void addEcoPoint(String memberId, Long howMuch) {
		Member member = memberService.findMemberById(memberId);
		member.setEco_point(member.getEco_point() + howMuch);
		memberService.save(member);
	}
}
