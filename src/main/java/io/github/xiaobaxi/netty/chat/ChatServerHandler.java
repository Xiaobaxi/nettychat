package io.github.xiaobaxi.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Created by xiaobaxi on 2017/4/26.
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        for (Channel c: channels) {
            if (c != channelHandlerContext.channel()) {
                c.writeAndFlush("[" + channelHandlerContext.channel().remoteAddress() + " receive a message] " + s + '\n');
            } else {
                c.writeAndFlush("[I send a message] " + s + '\n');
            }
        }

        if ("bye".equals(s.toLowerCase())) {
            channelHandlerContext.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

}
