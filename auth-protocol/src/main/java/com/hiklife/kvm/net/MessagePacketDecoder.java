package com.hiklife.kvm.net;


import com.hiklife.kvm.protocol.packet.KVMPacket;
import com.hiklife.kvm.utils.DataProcessingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

@Slf4j
public class MessagePacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        byte[] bytes = new byte[buffer.readableBytes()];
        int readerIndex = buffer.readerIndex();
         buffer.getBytes(readerIndex, bytes);
         log.debug("receive data,{}", DataProcessingUtil.bytesToHexString(bytes));
         KVMPacket packet = new KVMPacket(buffer);
         //log.debug("<decode>: decode untreated data,length:{}", buffer.readableBytes());
         out.add(packet);
    }
}
