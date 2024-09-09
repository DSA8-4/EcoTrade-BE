package com.example.board.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class SalesDTO {
	  private Long productId;
	    private String title;
	    private String contents;
	    private Long price;
	    private LocalDateTime createdTime;

}
