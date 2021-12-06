package com.hiklife.kvm;


import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.protocol.packet.KVMPacket;

public interface ReadHandler {
    /**
     * device-->server
     * 设备信息
     */
    void readNotifyKVMInfo(KVMPacket packet) throws InvalidKVMMessageException;
    /**
     * device-->server
     * 连接信息
     */
    void readNotifyKVMCon(KVMPacket packet) throws InvalidKVMMessageException;

}
