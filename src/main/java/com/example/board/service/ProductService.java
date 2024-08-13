package com.example.board.service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.AttachedImage;
import com.example.board.model.product.Product;
import com.example.board.repository.ImageRepository;
import com.example.board.repository.ProductRepository;
import com.example.board.util.ImageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductService {
	
	
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;

	
	//상품 등록
	@Transactional
	public Product uploadProduct(Product product, MultipartFile[] files) {
	    // 상품 저장
	    Product savedProduct = productRepository.save(product);

	    // 파일 처리
	    if (files != null) {
	        for (MultipartFile file : files) {
	            // 파일 저장 로직 추가
	            AttachedImage attachedImage = new AttachedImage();
	            attachedImage.setProduct(savedProduct);
	            attachedImage.setImage_size(file.getSize()); // 예시: 파일 데이터를 byte[]로 변환
	            // 이미지 메타데이터 설정
	            attachedImage.setSaved_image(file.getOriginalFilename());
	            
//	            attachedImage.setFileType(file.getContentType());
	            imageRepository.save(attachedImage); // 이미지 저장
	            
	        }
	    }

	    return savedProduct;
	}
	
	//상품 상세 검색
	public Product findProduct(Long id) {
		Optional<Product> product = productRepository.findById(id);
		return product.orElse(null);
	}
	
	//상품 수정
	@Transactional
	public void updateProduct(Product updateProduct, boolean isFileRemoved, MultipartFile file) {
		Product findProduct = findProduct(updateProduct.getProduct_id());
		
		findProduct.setTitle(updateProduct.getTitle());
		findProduct.setContents(updateProduct.getContents());
		findProduct.setPrice(updateProduct.getPrice());
	}
	//게시물 삭제
	@Transactional
	public void removeProduct(Product product) {
		productRepository.deleteById(product.getProduct_id());
	}
	

	//게시글 전체 목록
	public Page<Product> findAll(Pageable pageable) {
	    Page<Product> page = productRepository.findAll(pageable);
	    page.forEach(product -> {
	        List<AttachedImage> images = (List<AttachedImage>) imageRepository.findByProduct(product);
	        product.setImages(images); // 이미지 설정
	    });
	    return page;
	}

		public AttachedImage findFileByProductId(Product product) {
			AttachedImage attachedImage = imageRepository.findByProduct(product);
			return attachedImage;
		}

		public AttachedImage findFileByAttachedImageId(Long id) {
			AttachedImage attachedImage = imageRepository.findById(id).get();
			return attachedImage;
		}

		public int getTotal() {
			return (int)imageRepository.count();
		}

		public Page<Product> findSearch(String searchText, Pageable pageable) {
			Page<Product> searchList = productRepository.findByTitleContaining(searchText, pageable);
			return searchList;
		}

	
}





