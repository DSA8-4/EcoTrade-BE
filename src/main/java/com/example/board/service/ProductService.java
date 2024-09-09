package com.example.board.service;

import com.example.board.model.member.Member;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductLikeRepository;
import com.example.board.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	private final MemberRepository memberRepository;
	private final ProductLikeRepository productLikeRepository;
	private final MemberService memberService;

	// 상품 등록
	@Transactional
    public Product uploadProduct(Product product, String memberId) {
        // 회원 ID로 Member 객체 조회
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            product.setMember(member); // Product에 Member 설정
            return productRepository.save(product);
        }
        throw new RuntimeException("Member not found");
    }


	@Transactional
	public void saveImages(List<Image> images) {
		imageRepository.saveAll(images);
	}

	// 상품 상세 검색
	public Product findProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}

//	@Transactional
//	public boolean likeProduct(Long productId, String memberId) {
//		Product product = findProduct(productId);
//		Member member = memberService.findMemberById(memberId);  // Assuming you have a service to find the member
//
//		if (product != null && member != null) {
//			// Check if the user already liked the product
//			boolean alreadyLiked = likeRepository.existsByProductAndMember(product, member);
//
//			if (!alreadyLiked) {
//				// Add a new like
//				Like like = new Like();
//				like.setProduct(product);
//				like.setMember(member);
//				like.setLikedAt(LocalDateTime.now());
//
//				likeRepository.save(like);
//				return true;
//			}
//		}
//		return false; // User already liked the product or product/member doesn't exist
//	}
	

	// 상품 수정
	@Transactional
	public void updateProduct(Product updateProduct, boolean isFileRemoved, MultipartFile file) {
		Product findProduct = findProduct(updateProduct.getProduct_id());

		findProduct.setTitle(updateProduct.getTitle());
		findProduct.setContents(updateProduct.getContents());
		findProduct.setPrice(updateProduct.getPrice());

	}

	// 게시물 삭제
	@Transactional
	public void removeProduct(Product product) {
		productRepository.deleteById(product.getProduct_id());
	}

	// 게시글 전체 목록
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public int getTotal() {
		return (int) imageRepository.count();
	}

	public List<Product> findSearch(String searchText) {
		return productRepository.findByTitleContaining(searchText);
	}

	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}

	public Optional<Product> findById(Long productId) {
		return productRepository.findById(productId);
	}

	public void save(Product product) {
		productRepository.save(product);
	}

}
