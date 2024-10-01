package com.example.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.util.MacroUtil;

@Service
public class AdminService {
	@Autowired
	private MacroUtil macroUtil;

	// Admin 계정 생성 메서드
	public void createAdminAccount() {
		macroUtil.registerAdmin();
	}

	// 기존의 매크로 실행 (회원 및 상품 등록)
	public void runMacro() {
		macroUtil.registerMembersAndProducts();
	}
	
	public void addEcoPoint(String who, Long howMuch) {
		macroUtil.addEcoPoint(who, howMuch);
	}
}