package com.xy.gateway.netty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.xy.gateway.reactor.ReactorHandler;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import reactor.ipc.netty.http.server.HttpServer;

/**
 * netty server
 * 
 * @author xiongyan
 * @date 2018年1月15日 下午6:04:10
 */
//@Component
@Slf4j
public class ReactorServer {

	@Autowired
	private ReactorHandler reactorHandler;
	
	/**
	 * Channel
	 */
	private Channel channel;

	/**
	 * 端口号
	 */
	@Value("${server.port}")
	private int port;

	/**
	 * 启动netty 服务器
	 * 
	 * @throws InterruptedException
	 */
	@PostConstruct
	public void init() throws InterruptedException {
		RouterFunction<ServerResponse> routerFunction = routingFunction();
		HttpHandler httpHandler = RouterFunctions.toHttpHandler(routerFunction);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer server = HttpServer.create(port);
		channel = server.newHandler(adapter).block().channel();
		log.info("http server start success");
		channel.closeFuture().sync();
	}
	
	/**
	 * 关闭netty 服务
	 */
	@PreDestroy
	public void destroy() {
		if (null != channel) {
			channel.closeFuture().syncUninterruptibly();
			channel = null;
		}
	}

	private RouterFunction<ServerResponse> routingFunction() {
		return RouterFunctions.route(RequestPredicates.GET("/forward"), reactorHandler::forward)
				.andRoute(RequestPredicates.GET("/hello"), reactorHandler::hello)
				.andRoute(RequestPredicates.GET("/byId/{id}"), reactorHandler::byId)
				.andRoute(RequestPredicates.GET("/push"), reactorHandler::push)
				.andRoute(RequestPredicates.GET("/pushjson"), reactorHandler::pushJson);
	}

}	
