package com.xy.gateway.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class User {
	
	private String name;
	
	private String pass;
	
	private Date time;
}
