package com.hiklife.kvm.net;


import com.hiklife.kvm.KVMSession;
import com.hiklife.kvm.protocol.packet.KVMPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TCPServerHandler extends ChannelInboundHandlerAdapter {
    public static final AttributeKey<KVMSession> KEY = AttributeKey.valueOf("IO");
    public static Map<String, KVMSession> deviceSessionMap = new ConcurrentHashMap<>(100);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object source) throws Exception {
        log.debug("<channelRead>: channel recv data, starting to handle it!");
        KVMPacket recvMsg  = (KVMPacket)source;
        KVMSession session = ctx.channel().attr(KEY).get();
        if(session == null) {
            log.debug("<channelRead>: session is null, close the channel");
            ctx.channel().close();
            return;
        }
        session.handle(recvMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("<exceptionCaught>: close the channel. {}", cause.toString());
        super.exceptionCaught(ctx, cause);
        KVMSession session = new KVMSession(ctx.channel());
        if(session.getDevice()!=null){
            session.kickDeviceOff(session.getDevice());
            deviceSessionMap.remove(session.getDevice().getSn());
        }
        ctx.channel().close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("<exceptionCaught>: {}",String.format("client out... : %s", ctx.channel()));
        KVMSession session = ctx.channel().attr(KEY).getAndSet(null);
        if(!session.isDuplication() && session.getDevice()!=null){
            session.kickDeviceOff(session.getDevice());
            deviceSessionMap.remove(session.getDevice().getSn());
        }
        ctx.channel().close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        super.channelRegistered(ctx);
        log.info("<channelRegistered>:{}",String.format("client registered...：   %s ...", ctx.channel()));
        DeviceSession deviceSession = new DeviceSession(ctx.channel());
        ctx.channel().attr(KEY).set(deviceSession);
    }
}
