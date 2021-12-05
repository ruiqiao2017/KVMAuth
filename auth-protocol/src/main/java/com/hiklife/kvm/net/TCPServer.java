package com.hiklife.kvm.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TCPServer {
    private static final int BOSS_GROUP_THREAD_NUMBER = 1;
    private static final int WORKER_GROUP_THREAD_NUMBER = 2;
    private static final int SO_BACKLOG_LENGTH = 1024;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_THREAD_NUMBER);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(WORKER_GROUP_THREAD_NUMBER);
    private final TCPConfig config;
    private boolean isRunning = false;
    public TCPServer(TCPConfig config){
        this.config = config;
    }
    public void init() throws Exception{
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class)
                .childHandler(new TCPServerChannelInitializer(config.getHeartbeatTime()))
                .option(ChannelOption.SO_BACKLOG, SO_BACKLOG_LENGTH)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture channelFuture = serverBootstrap.bind(config.getPort()).sync();
        if(channelFuture.isSuccess()){
            log.info("<init>: TCPServer start success");
            isRunning = true;
        } else {
            log.info("<init>: TCPServer start fail");
        }
    }

    public synchronized void startServer() {
        if (this.isRunning) {
            log.info("<startServer>: TCPServer is running");
            return;
        }
        try {
            this.init();
        } catch (Exception ex) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.error("<startServer>: startServer error, {}", ex.toString());
        }
    }

    public synchronized void stopServer() {
        if (!this.isRunning) {
            log.error("<stopServer>: is not running");
            return;
        }
        try {
            Future<?> future = this.workerGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("<stopServer>:workerGroup,{}", future.cause().toString());
            }
            future = this.bossGroup.shutdownGracefully().await();
            if (!future.isSuccess()) {
                log.error("<stopServer>:bossGroup,{}", future.cause().toString());
            }
            this.isRunning = false;
        } catch (InterruptedException e) {
            log.error("<stopServer>:workerGroup,{}", e.toString());
        }
        this.log.info("<stopServer>:TCPServer is stopped");
    }
}
