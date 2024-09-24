package com.example.board.dto;

import java.time.LocalDate;

import com.example.board.model.member.Area;
import com.example.board.model.member.ProfileImage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
	private String name;
	private String email;
	private String newPassword; // 비밀번호 변경 시 필요한 경우
	private ProfileImage profileImage; // 프로필 이미지 정보
	private Area area; // 지역

}
