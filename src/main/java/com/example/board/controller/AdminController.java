package com.example.board.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.service.AdminService;

@RestController
public class AdminController {
	@Autowired
	private AdminService adminService;

	@PostMapping("/run-macro")
	public String runMacro() {
		adminService.runMacro();
		return "매크로 실행 완료!";
	}

}
