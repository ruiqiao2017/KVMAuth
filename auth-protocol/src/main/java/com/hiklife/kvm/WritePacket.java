package com.hiklife.kvm;


import com.cetiti.hikbot.constant.Constant;
import com.cetiti.hikbot.device.protocol.packet.*;
import org.apache.commons.lang3.ArrayUtils;

public abstract class WritePacket {
    protected final int uuid;
    protected final HikBotWritePacket packet;

    public WritePacket(int uuid, HikBotWritePacket packet) {
        this.uuid = uuid;
        this.packet = packet;
    }

    public byte[] encode() {
        byte[] body;
        if(packet!=null) {
            body = packet.getType()==Constant.PACKET_OUT_REQUEST?packet.getRequestPacket().encode():packet.getResponsePacket().encode();
        } else {
            body = new byte[0];
        }
        byte[] header = HikBotPacketHeader.encode(uuid, getTag().getCode(), (short)(body.length));
        return ArrayUtils.addAll(header, body);
    }

    protected abstract HikBotPacketTag getTag();

    public int getUuid() {
        return uuid;
    }
    public HikBotWritePacket getPacket() {
        return packet;
    }

}
