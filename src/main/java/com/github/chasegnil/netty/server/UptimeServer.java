package com.github.chasegnil.netty.server;

import com.github.chasegnil.netty.server.handler.UptimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public final class UptimeServer {

    private static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    private static final UptimeServerHandler handler = new UptimeServerHandler();

    private UptimeServer() {
    }

    public static void main(String[] args) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 配置server
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new UptimeServerHandler());
                    }
                });

            // 绑定并开始接受传入的连接
            ChannelFuture future = b.bind(PORT).sync();

            // 等待服务器套接字关闭
            future.channel().closeFuture().sync();
        } finally {
            // 关闭所有事件循环以终止所有线程
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }
}
