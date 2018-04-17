package com.xy.gateway.reactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {
	
	@Autowired
	private ReactorHandler reactorHandler;

	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions.route(RequestPredicates.GET("/forward"), reactorHandler::forward)
				.andRoute(RequestPredicates.GET("/hello"), reactorHandler::hello)
				.andRoute(RequestPredicates.GET("/byId/{id}"), reactorHandler::byId)
				.andRoute(RequestPredicates.GET("/push"), reactorHandler::push)
				.andRoute(RequestPredicates.GET("/pushjson"), reactorHandler::pushJson);
	}
	
}
