package com.example.board.dto;

import java.time.LocalDate;

import com.example.board.model.member.Area;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
	private String name;
	private String email;
	private Area area; // 지역
}
