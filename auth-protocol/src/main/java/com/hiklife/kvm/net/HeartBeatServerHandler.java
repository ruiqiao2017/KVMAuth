package com.hiklife.kvm.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    private int lossConnectCount = 0;
    private TCPConfig config;
    public HeartBeatServerHandler(TCPConfig config) {
        super();
        this.config = config;
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("<userEventTriggered>: heartbeat event is triggered");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                lossConnectCount ++;
                if (lossConnectCount >= config.getMaxLossConnectCount()){
                    lossConnectCount = 0;
                    log.info("<userEventTriggered>: heartbeat event is triggered, close the session");
                    ctx.channel().close();
                }
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }
}
