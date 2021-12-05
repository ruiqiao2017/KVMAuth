package com.hiklife.kvm.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class TCPServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final int heartbeatTime;
    private static final int maxFrameLength = 51712;
    private static final int lengthFieldOffset = 6;
    private static final int lengthFieldLength = 2;
    private static final int LengthAdjustment = 0;
    private static final int initialBytesToStrip = 0;
    public TCPServerChannelInitializer(int heartbeatTime){
        super();
        this.heartbeatTime = heartbeatTime;
    }
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(2);
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        log.debug("<initChannel>: {}, link starting", socketChannel.remoteAddress().getHostString());
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new IdleStateHandler(heartbeatTime, 0, 0, TimeUnit.SECONDS))
                .addLast(new LengthFieldBasedFrameDecoder(maxFrameLength, lengthFieldOffset, lengthFieldLength, LengthAdjustment, initialBytesToStrip))
                .addLast(new MessagePacketDecoder(), new MessagePacketEncoder())
                .addLast(group, "handler",new TCPServerHandler())
                .addLast("heartBeatHandler",new HeartBeatServerHandler());
    }
}
