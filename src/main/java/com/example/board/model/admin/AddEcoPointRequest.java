package com.example.board.model.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AddEcoPointRequest {
	String memberId;
	Long ecoPoint;
}
