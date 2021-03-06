package com.hiklife.kvm.protocol.packet;

import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.utils.CRC16;
import com.hiklife.kvm.utils.DataProcessingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
@Data
public class KVMPacket {
    private final static byte[] FRAME_HEAD = new byte[]{(byte) 0XFF,(byte)0XFE,(byte)0XFD,(byte)0XFC};
    private byte[] head;
    private int payloadLength;
    private PacketCMD cmd;
    private List<PacketItem> payloadData;
    private short crc;

    public KVMPacket(ByteBuf byteBuf) throws InvalidKVMMessageException {
        head = new byte[4];
        byteBuf.readBytes(head,0,4);
        if(DataProcessingUtil.compareBytes(head,FRAME_HEAD)){
            throw new InvalidKVMMessageException("FRAME_HEAD check");
        }
        byte[] crcByteBuf = new byte[byteBuf.readableBytes() - 4];
        byteBuf.getBytes(0,crcByteBuf,0,2);

        payloadLength = byteBuf.readInt();
        byte cmdByte = byteBuf.readByte();
        cmd = PacketCMD.parse(cmdByte);
        if(cmd.equals(PacketCMD.NONE)) {
            throw new InvalidKVMMessageException("CMD check");
        }
        payloadData = new ArrayList<>();
        int readLength = 0;
        while(readLength < (payloadLength - 1)){
            byte no = byteBuf.readByte();
            int length = byteBuf.readInt();
            byte[] data = new byte[length];
            byteBuf.readBytes(data,0,length);
            PacketItem packetItem = new PacketItem(no,length,data);
            payloadData.add(packetItem);
            readLength = readLength + length + 3;
        }
        crc = byteBuf.readShort();
        //checkCRC
        short crcValue = CRC16.calCrc16(crcByteBuf);
        if(crc != crcValue) {
            throw new InvalidKVMMessageException("CRC check");
        }
    }

    public static void main(String[] args){
        ByteBuf buf = Unpooled.buffer(10);
        buf.writeBytes(FRAME_HEAD);
        byte[] var1 = new byte[4];
        System.out.println(DataProcessingUtil.byteToHexString(buf.getByte(0)));
        System.out.println(DataProcessingUtil.byteToHexString(buf.getByte(1)));
        System.out.println(DataProcessingUtil.byteToHexString(buf.getByte(2)));
        System.out.println(DataProcessingUtil.byteToHexString(buf.getByte(3)));
        buf.readBytes(var1,0,4);
        System.out.println(DataProcessingUtil.bytesToHexString(var1));
    }

}
