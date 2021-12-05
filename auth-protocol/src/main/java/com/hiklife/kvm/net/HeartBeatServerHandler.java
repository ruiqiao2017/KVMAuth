package com.hiklife.kvm.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatServerHandler extends ChannelInboundHandlerAdapter {
    //连续MAX_LOSS_CONNECT_TIMES次未来心跳时间收到数据，则断开连接
    private static final int MAX_LOSS_CONNECT_TIMES = 4;
    private int lossConnectCount = 0;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.debug("<userEventTriggered>: heartbeat event is triggered");
        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state()== IdleState.READER_IDLE){
                lossConnectCount ++;
                if (lossConnectCount >= MAX_LOSS_CONNECT_TIMES){
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
