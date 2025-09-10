package com.caerus.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserNotification {
	
	private Long userId;
	private String email;
	private String subject;
	private String message;
	private String phoneNumber;
	private String whatsappNumber;
	
	
	
	

}