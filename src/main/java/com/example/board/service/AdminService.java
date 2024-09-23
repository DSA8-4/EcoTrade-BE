package com.example.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.board.util.MacroUtil;

@Service
public class AdminService {
	@Autowired
	private MacroUtil macroUtil;

	public void runMacro() {
		macroUtil.registerMembersAndProducts();
	}

}
