package com.xy.gateway.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xy.gateway.pojo.Result;
import com.xy.gateway.pojo.User;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hello")
public class HelloController {

	@GetMapping("/world")
	public Mono<String> hello() {
		return Mono.just("Welcome to reactive world ~");
	}
	
	@GetMapping("/{id}")
	public Mono<Map<String, Object>> helloId(@PathVariable Integer id) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", "熊焱");
		map.put("id", id);
		return Mono.just(map);
	}
	
	@GetMapping("/result")
	public Mono<Result<User>> result() {
		User user = new User();
		user.setName("熊焱");
		user.setPass("熊焱");
		user.setTime(new Date());
		return Mono.just(Result.success(user));
	}
	
}
