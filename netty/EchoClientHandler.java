package com.nettyOfficial;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * Created by yuan on 2017/8/25.
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(Unpooled.buffer(4).writeBytes("1234".getBytes()));
    }


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.toString());
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
    }
}
