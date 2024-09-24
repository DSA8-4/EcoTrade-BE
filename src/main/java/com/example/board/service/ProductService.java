package com.example.board.service;

import com.example.board.model.member.Member;
import com.example.board.model.product.Image;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductLike;
import com.example.board.model.product.Purchase;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.MemberRepository;
import com.example.board.repository.ProductLikeRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.repository.PurchaseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	private final MemberRepository memberRepository;
	private final ProductLikeRepository productLikeRepository;
	private final PurchaseRepository purchaseRepository;
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
    public void savePurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
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

	@Transactional
	public boolean productLike(Long productId, String memberId) {
		Product product = findProduct(productId);
		Member member = memberService.findMemberById(memberId);  // Assuming you have a service to find the member

		if (product != null && member != null) {
			// Check if the user already liked the product
			boolean alreadyLiked = productLikeRepository.existsByProductAndMember(product, member);

			if (!alreadyLiked) {
				// Add a new like
				ProductLike productlike = new ProductLike();
				productlike.setProduct(product);
				productlike.setMember(member);
				productlike.setLikedAt(LocalDateTime.now());

				productLikeRepository.save(productlike);
				return true;
			}
		}
		return false; // User already liked the product or product/member doesn't exist
	}
	

	// 상품 수정
	@Transactional
	public void updateProduct(Product updateProduct, boolean isFileRemoved, MultipartFile file) {
		Product findProduct = findProduct(updateProduct.getId());

		findProduct.setTitle(updateProduct.getTitle());
		findProduct.setContents(updateProduct.getContents());
		findProduct.setPrice(updateProduct.getPrice());

	}

	// 게시물 삭제
	@Transactional
	public void removeProduct(Product product) {
		productRepository.deleteById(product.getId());
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
	
	public boolean isProductLiked(Long productId, String memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        return productLikeRepository.existsByProductAndMember(product, member);
    }

    @Transactional
    public void addProductLike(Long productId, String memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        if (!productLikeRepository.existsByProductAndMember(product, member)) {
            ProductLike productLike = new ProductLike();
            productLike.setProduct(product);
            productLike.setMember(member);
            productLikeRepository.save(productLike);
            product.addHeart(); // Product의 하트 수 증가
            productRepository.save(product); // 업데이트된 Product 저장
        }
    }

    @Transactional
    public void removeProductLike(Long productId, String memberId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품이 존재하지 않습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        ProductLike productLike = productLikeRepository.findByProductAndMember(product, member)
                .orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));

        productLikeRepository.delete(productLike);
        product.removeHeart(); // Product의 하트 수 감소
        productRepository.save(product); // 업데이트된 Product 저장
    }
    
}
