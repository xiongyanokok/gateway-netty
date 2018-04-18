package com.xy.gateway.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.xy.gateway.controller.HelloController;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReactorTest {
	
	@Autowired
	private HelloController helloController;

	@Test
	public void test() {
		Mono<String> mono = helloController.hello();
		System.out.println(mono.block());
	}
}
