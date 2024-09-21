package com.example.board.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import com.example.board.model.member.ProfileImage;


@Getter @Setter
public class MemberProfileDto {
	 private String member_id;
	    private String name;
	    private String email;
	    private Long eco_point;
	    private String profileImageUrl;

	    // 생성자
	    public MemberProfileDto(String member_id, String name, String email, Long eco_point, ProfileImage profileImage) {
	        this.member_id = member_id;
	        this.name = name;
	        this.email = email;
	        this.eco_point = eco_point;
	        this.profileImageUrl = profileImage != null ? profileImage.getUrl() : null; // ProfileImage에서 URL 가져오기
	    }

}
