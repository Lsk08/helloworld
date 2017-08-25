package com.nettyOfficial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by yuan on 2017/8/25.
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //简单地丢弃所有的信息
        //Handler有义务释放接受的消息，例如
        /*
        try{
        process(msg);
        }finally{
        ReferenceCountUtil.release(msg);
        }
        * */
        System.out.println(msg.toString());
        ctx.write(msg);
        ((ByteBuf) msg).release();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Nio的异常处理，一般是记录Log，发送一个错误response，然后关闭连接 ctx.close();
        cause.printStackTrace();
        ctx.close();
    }
}
