package com.example.board.model.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class Member {
	
	@Id
	@Column(length=20)
	private String member_id;
	

	@Column(length=64, nullable = false)
	private String password;
	
	@Column(length=50, nullable = false)
	private String name;
	
	
	@Column(length=100)
	private String email;
	
	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
    private ProfileImage profileImage; // ProfileImage 객체 추가
	private Long eco_point = 0L;
}