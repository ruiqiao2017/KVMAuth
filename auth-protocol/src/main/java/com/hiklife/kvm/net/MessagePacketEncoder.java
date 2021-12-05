package com.hiklife.kvm.net;

import com.hiklife.kvm.WritePacket;
import com.hiklife.kvm.utils.DataProcessingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessagePacketEncoder extends MessageToByteEncoder<WritePacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, WritePacket msg, ByteBuf out){
        byte[] bytes = msg.encode();
        out.writeBytes(bytes);
        log.debug("<encode>:MessagePacketEncoder is send,length:{},data:{}", bytes.length,
                DataProcessingUtil.bytesToHexString(bytes));
    }
}
