package com.example.board.model.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@Column(length = 1024)
    private String profileImageUrl; // URL of the profile image
	
	
	private Long eco_point = 0L;
	
	//지역
	@Enumerated(EnumType.STRING)
	private Area area;
}