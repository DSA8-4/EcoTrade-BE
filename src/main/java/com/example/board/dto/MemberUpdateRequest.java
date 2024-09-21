package com.example.board.dto;

import java.time.LocalDate;

import com.example.board.model.member.Area;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberUpdateRequest {
	 private String name;
	 private String email;
	 private String newPassword; // 비밀번호 변경 시 필요한 경우
	 private Area area;
	    

}
