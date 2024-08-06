package com.example.board.model.member;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberJoinForm {
	@Size(min = 4, max = 20)
	private String member_id;
	
	@Size(min = 4, max = 20)
	private String password;
	
	@NotBlank(message = "닉네임을 지어주세요")
	private String name;
	
	@Past
	@NotNull(message = "생일을 골라주세요")
	private LocalDate birth;
	
	private String email;
	
	public static Member toMember(MemberJoinForm memberJoinForm) {
		Member member = new Member();
		
		member.setMember_id(memberJoinForm.getMember_id());
		member.setPassword(memberJoinForm.getPassword());
		member.setName(memberJoinForm.getName());
		member.setBirth(memberJoinForm.getBirth());
		member.setEmail(memberJoinForm.getEmail());
		
		return member;
	}
}