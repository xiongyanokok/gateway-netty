package com.xy.gateway.netty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * netty server
 * 
 * @author xiongyan
 * @date 2018年1月15日 下午6:04:10
 */
@Component
@Slf4j
public class HttpServer {
	
	/**
	 * 服务端处理器
	 */
	@Autowired
	private HttpServerInboundHandler httpServerInboundHandler;

	/**
	 * 主线程池
	 */
	private EventLoopGroup bossGroup;

	/**
	 * 工作线程池
	 */
	private EventLoopGroup workerGroup;
	
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
		bossGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("NettyServerBoss", Boolean.TRUE));
		workerGroup = new NioEventLoopGroup(0, new DefaultThreadFactory("NettyServerWorker", Boolean.TRUE));
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(NioServerSocketChannel.class);
		b.option(ChannelOption.SO_BACKLOG, 1024);
		b.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);
		b.childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE);
		b.childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE);
		b.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				// 服务端，对请求解码
				ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
				// 聚合器，把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse
				ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
				// 服务端，对响应编码
				ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
				// 块写入处理器
				ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
				// 自定义服务端处理器
				ch.pipeline().addLast("server-handler", httpServerInboundHandler);
			}
		});
		channel = b.bind(port).sync().channel();
		log.info("netty server start success");
		channel.closeFuture().sync();
	}

	/**
	 * 关闭netty 服务
	 */
	@PreDestroy
	public void destroy() {
		if (null != workerGroup) {
			workerGroup.shutdownGracefully();
			workerGroup = null;
		}
		if (null != bossGroup) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}
		if (null != channel) {
			channel.closeFuture().syncUninterruptibly();
			channel = null;
		}
	}
	
}
