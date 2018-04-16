package com.xy.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动服务
 * 
 * @author xiongyan
 * @date 2017年12月17日 上午11:01:57
 */
@SpringBootApplication
@ComponentScan("com.xy")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
}