package io.github.xiaobaxi.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by xiaobaxi on 2017/4/24.
 */
public class ChatClient {

    private final String host;

    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();


        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            ChannelFuture lastWriteFuture = null;

            while (true) {
                String line = br.readLine();
                if(null == line) {
                    break;
                }
                lastWriteFuture = channel.writeAndFlush(line + "\r\n");

                if ("bye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }

                if (lastWriteFuture != null) {
                    lastWriteFuture.sync();
                }
            }
        } finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new ChatClient("localhost", 8000).run();
    }

}
