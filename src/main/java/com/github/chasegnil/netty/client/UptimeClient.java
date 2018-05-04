package com.github.chasegnil.netty.client;

import com.github.chasegnil.netty.client.handler.UptimeClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * 重连 client
 */
public class UptimeClient {

    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    // 在重新连接尝试前睡5秒。
    public static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    // 当服务器10秒不发送消息时重新连接
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "10"));

    private static final UptimeClientHandler handler = new UptimeClientHandler();
    private static final Bootstrap b = new Bootstrap();

    public static void connect() {
        b.connect().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    handler.startTime = -1;
                    handler.println("Failed to connect: " + future.cause());
                }
            }
        });
    }

    public static void main(String[] args) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
            .channel(NioSocketChannel.class)
            .remoteAddress(HOST, PORT)
            .handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, 0 ,0), handler);
                }
            });
        b.connect();
    }
}
