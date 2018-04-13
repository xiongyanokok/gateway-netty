package com.xy.gateway.netty;

import java.time.Duration;

import org.springframework.stereotype.Component;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientResponse;

/**
 * 服务端处理器
 * 
 * @author xiongyan
 * @date 2018年1月10日 上午10:20:09
 */
@Component
@Sharable
@Slf4j
public class HttpServerInboundHandler extends SimpleChannelInboundHandler<HttpRequest> {

	/**
	 * 接收消息
	 * 
	 * @param ctx
	 * @param request
	 * @throws Exception
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
		String result = "{\"code\":" + 10001 + ", \"msg\":\"系统错误\"}";
		String uri = request.uri();
		if ("/favicon.ico".equals(uri)) {
			return;
		}
		try {
			Mono<HttpClientResponse> mono = HttpClient.create().get("https://search.jd.com/Search?keyword=食用油5l&enc=utf-8");
			result = mono.flatMapMany(s -> s.receive().asString()).reduce(String::concat).block(Duration.ofSeconds(5));
		} catch (Exception e) {
			result = "{\"code\":" + 10001 + ", \"msg\":\"系统错误\"}";
			log.error("【{}】聚合失败:", uri, e);
		} finally {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(result, CharsetUtil.UTF_8));
			response.headers().set("Content-Type", "application/json; charset=UTF-8");
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
}
