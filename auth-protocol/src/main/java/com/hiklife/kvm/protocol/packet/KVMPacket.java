package com.hiklife.kvm.protocol.packet;

import io.netty.buffer.ByteBuf;

/**
 * #1# PACKET = |FRAMEHEAD|PAYLOADLEN|PAYLOAD|CRC|
 * #2# PAYLOAD = |CMD|PAYLOADHEAD|PAYLOADDATA
 *     命令实体由命令CMD和实体头PAYLOADHEAD和命令实体数据PAYLOADDATA组成。
 * #3# PAYLOADHEAD和PAYLOADDATA由多个项目ITEM组成。
 *    每个项目ITEM由项目编号、数据长度、数据组成。
 *    ITEM_NO|DATALEN|DATA …… ITEM_NO|DATALEN|DATA
 * #4# 基本数据包包含4个部分：
 *     FRAMEHEAD  4 bytes  FF FE FD FC
 *     PAYLOADLEN  2 bytes  PAYLOAD长度 小端模式
 *     PAYLOAD   命令数据
 *     CRC：CRC16
 *     4 bytes（unsigned long） PAYLOADLEN | PAYLOAD的CRC16校验 小端模式
 */
public class KVMPacket {
    private final static byte[] FRAME_HEAD = new byte[]{(byte) 0XFF,(byte)0XFE,(byte)0XFD,(byte)0XFC};
    private long payloadLength;
    private PacketCMD cmd;
    private PacketItem[] payloadHead;
    private PacketItem[] payloadData;
    private byte[] crc;

    public KVMPacket(ByteBuf byteBuf) {

    }

    public static boolean verification(ByteBuf byteBuf){
        return true;
    }
}
