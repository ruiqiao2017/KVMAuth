package com.hiklife.kvm.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TCPServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final TCPConfig config;
    private static final int maxFrameLength = 51712;
    private static final int lengthFieldOffset = 4;
    private static final int lengthFieldLength = 2;
    private static final int LengthAdjustment = 4;
    private static final int initialBytesToStrip = 0;
    public TCPServerChannelInitializer(TCPConfig config){
        super();
        this.config = config;
    }
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(2);
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.debug("<initChannel>: {}, link starting", socketChannel.remoteAddress().getHostString());
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(config.getHeartbeatTime(), 0, 0, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, LengthAdjustment, initialBytesToStrip))
                .addLast(new MessagePacketDecoder(), new MessagePacketEncoder())
                .addLast(group, "handler",new TCPServerHandler())
                .addLast("heartBeatHandler",new HeartBeatServerHandler(config));
    }
}
