package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private AdminService adminService;

	// Admin 계정 생성 매크로 실행
    @PostMapping("/create-admin")
    public String createAdminAccount() {
        adminService.createAdminAccount();  // admin 생성
        return "Admin 계정 생성 완료!";
    }

    // 회원 및 상품 생성 매크로 실행
    @PostMapping("/run-macro")
    public String runMemberAndProductMacro() {
        adminService.runMacro();  // 회원 및 상품 생성
        return "회원 및 상품 생성 매크로 실행 완료!";
    }
}
