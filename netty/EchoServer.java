package com.nettyOfficial;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by yuan on 2017/8/25.
 */
public class EchoServer {

    private int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void run(){
        //bossGroup相当于selector，监听channel，并且接受请求accept,
        //具体的线程数量可以定义，不写就是默认  DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
        //也就是max(1,cpu核心数量*2)
        //具体的任务将由这个EnventLoopGroup里的线程分担，由具体的实现决定，例如这个NioEventLoopGroup就是按照尽量让每个线程的时间工作量一样的原则分配工作
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        //workGroup相当于worker，负责处理请求，包括 read->decode->process->encode->send
        EventLoopGroup workGroup=new NioEventLoopGroup();
        try {
            //服务引导器，创建并且启动服务
        ServerBootstrap b=new ServerBootstrap();
        //Netty的流式配置服务，group配置 ServerChannel和childChannel的线程，channel配置ServerChannel的实现类，childHandler配置绑定的handler，也就是这个App的主要功能
        // ChennelInitializer负责初始化其他的所有Handler,随着App的复杂可以把他单独放在一个class里，而不是采用匿名类
        b.group(bossGroup,workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new EchoServerHandler());
            }
        }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
        //option和childoption分别配置serverChannel和它的childChannel
            //具体含义 todo

            ChannelFuture future=b.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        int port=10080;
        new EchoServer(port).run();

    }
}
