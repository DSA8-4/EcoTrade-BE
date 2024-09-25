package com.example.board.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

import com.example.board.model.member.Area;



@Getter @Setter
public class MemberProfileDto {
	 private String member_id;
	    private String name;
	    private String email;
	    private Long eco_point;
	    private String profileImageUrl;
	    private Area area;

	    // 생성자
	    public MemberProfileDto(String member_id, String name, String email, Long eco_point,  String profileImageUrl, Area area) {
	        this.member_id = member_id;
	        this.name = name;
	        this.email = email;
	        this.eco_point = eco_point;
	        this.profileImageUrl = profileImageUrl;
	        this.area = area;
	    }

}
