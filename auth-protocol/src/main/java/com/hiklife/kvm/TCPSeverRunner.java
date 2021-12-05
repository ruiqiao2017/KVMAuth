package com.hiklife.kvm;

public interface TCPSeverRunner {
    void startServer();
    void stopServer();
    /**
     * server--kvm
     * 主动发起：配置设备模式
     */
    void writeRequestTest(Device device) throws InvalidHikBotMessageException;
}
