package com.example.board.model.member;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class LoginForm {
	
	@Size(min=4, max=20)
	private String member_id;
	
	@Size(min=4, max=20)
	private String password;

}
