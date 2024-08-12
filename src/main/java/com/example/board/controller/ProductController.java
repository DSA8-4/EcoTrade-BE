package com.example.board.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.model.product.AttachedImage;
import com.example.board.model.product.Product;
import com.example.board.model.product.ProductWriteForm;
import com.example.board.repository.ProductRepository;
import com.example.board.service.ProductService;
import com.example.board.util.ImageService;

import com.example.board.util.PageNavigator;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ProductController {
	private final ImageService imageService;
	private final ProductService productService;

	@Value("${file.upload.path}")
    private String uploadPath;
	
	
	//상품 등록

	@PostMapping("/new")
	public ResponseEntity<Product> newProduct(
	        @RequestParam("title") String title,
	        @RequestParam("contents") String contents,
	        @RequestParam("price") Long price,
	        @RequestParam(value = "files", required = false) MultipartFile[] files) {

	    Product product = new Product();
	    product.setTitle(title);
	    product.setContents(contents);
	    product.setPrice(price);

	    try {
	        // 상품 등록 처리 (파일 업로드 포함)

	        Product createdProduct = productService.uploadProduct(product, files);

	        return ResponseEntity.ok(createdProduct);
	    } catch (Exception e) {
	        log.error("상품 등록 중 오류 발생", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	    
	
	    

	
	//게시글 목록 출력
	@GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "searchText", defaultValue = "") String searchText) {

        Map<String, Object> response = new HashMap<>();

        Page<Product> productList;
        int totalRecordsCount;
        int totalPageCount;

        if (!searchText.isEmpty()) {
            productList = productService.findSearch(searchText, pageable);
            totalRecordsCount = (int) productList.getTotalElements();
            totalPageCount = productList.getTotalPages();
        } else {
            productList = productService.findAll(pageable);
            totalRecordsCount = (int) productList.getTotalElements();
            totalPageCount = productList.getTotalPages();
        }

        PageNavigator navi = new PageNavigator(10, 5, page, totalRecordsCount, totalPageCount);

        response.put("productList", productList.getContent());
        response.put("currentPage", productList.getNumber() + 1);
        response.put("totalItems", productList.getTotalElements());
        response.put("totalPages", productList.getTotalPages());
        response.put("navi", navi);
        response.put("searchText", searchText);

        return ResponseEntity.ok(response);
    }	
}
