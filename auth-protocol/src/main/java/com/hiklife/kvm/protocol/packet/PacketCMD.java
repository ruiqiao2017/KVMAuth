package com.hiklife.kvm.protocol.packet;

public enum PacketCMD {
    /**
     * MSG_NOTIFY:单向设备-server
    */
    //KVM-->Server:KVM上报数据
    MSG_NOTIFY_KVM_INFO((byte)0x01),
    //KVM-->Server:KVM连接上报数据
    MSG_NOTIFY_KVM_CON((byte)0x11),
    //无法识别CMD
    NONE((byte)0x00);
    private final byte cmd;
    PacketCMD(byte cmd) {
        this.cmd = cmd;
    }

    public static PacketCMD parse(byte cmd) {
        PacketCMD[] values = PacketCMD.values();
        for (PacketCMD value : values) {
            if (value.getCmd() == cmd) {
                return value;
            }
        }
        return PacketCMD.NONE;
    }

    public byte getCmd() {
        return cmd;
    }
}
