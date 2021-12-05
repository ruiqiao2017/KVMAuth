package com.hiklife.kvm;


import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.protocol.packet.KVMPacket;

public interface ReadHandler {
    /**
     * device-->server
     * 设备协议认证
     */
    void readRequestAuth(KVMPacket packet) throws InvalidKVMMessageException;
    /**
     * device-->server
     * 心跳
     */
    void readRequestHeartBeat(KVMPacket packet) throws InvalidKVMMessageException;

}
