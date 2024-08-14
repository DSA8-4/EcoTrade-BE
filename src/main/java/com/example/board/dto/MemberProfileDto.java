package com.example.board.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class MemberProfileDto {
	 private String member_id;
	    private String name;
	    private LocalDate birth;
	    private String email;
	    private Long eco_point;

	    // 생성자
	    public MemberProfileDto(String member_id, String name, LocalDate birth, String email, Long eco_point) {
	        this.member_id = member_id;
	        this.name = name;
	        this.birth = birth;
	        this.email = email;
	        this.eco_point = eco_point;
	    }

}
