package com.hiklife.kvm;

import com.hiklife.kvm.protocol.packet.normal.KVMInfo;
import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;

public interface TCPSeverRunner {
    void startServer();
    void stopServer();
    /**
     * server--kvm
     * 主动发起：配置设备模式
     */
    void writeRequestTest(KVMInfo device) throws InvalidKVMMessageException;
}
