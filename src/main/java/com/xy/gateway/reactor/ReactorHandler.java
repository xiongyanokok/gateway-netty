package com.xy.gateway.reactor;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.xy.gateway.pojo.User;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactorHandler {

	public Mono<ServerResponse> forward(ServerRequest request) {
		Mono<String> result = WebClient.create("https://www.baidu.com")
				.get().uri("/s?wd=熊焱")
				.accept(MediaType.APPLICATION_JSON_UTF8).retrieve()
				.bodyToMono(String.class)
				.timeout(Duration.ofMillis(100))
				.onErrorReturn("{\"error\":\"失败\"}");
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(result, String.class);
	}
	
	
	public Mono<ServerResponse> hello(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(Mono.just("{\"data\":\"hello\"}"), String.class);
	}
	
	public Mono<ServerResponse> byId(ServerRequest request) {
		String id = request.pathVariable("id");
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(Mono.just("{\"data\":\"hello\", \"id\":\""+id+"\"}"), String.class);
	}
	
	public Mono<ServerResponse> push(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(
				Flux.interval(Duration.ofSeconds(1)).map(l -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())), String.class);
	}
	
	public Mono<ServerResponse> pushJson(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_STREAM_JSON).body(fluxUser().delayElements(Duration.ofSeconds(2)), User.class);
	}
	
	private Flux<User> fluxUser() {
		User user = new User();
		user.setName("熊焱");
		user.setPass("熊焱");
		return Flux.just(user, user, user);
	}
	
}
