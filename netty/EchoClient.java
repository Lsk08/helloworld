package com.nettyOfficial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by yuan on 2017/8/25.
 */
public class EchoClient {

    public void run(){
        EventLoopGroup group=new NioEventLoopGroup();

        try {
        Bootstrap b=new Bootstrap();
        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new EchoClientHandler());
            }
        });

            ChannelFuture future=b.connect("localhost",10080).sync();

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        new EchoClient().run();
    }
}
