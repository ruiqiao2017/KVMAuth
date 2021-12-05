package com.hiklife.kvm.net;


import com.hiklife.kvm.KVMEvent;

public class TCPConfig {
    private final static int DEFAULT_HEARTBEAT_TIME = 15;
    private final static int MAX_LOSS_CONNECT_COUNT = 3;
    private int port;
    private int heartbeatTime;
    private int maxLossConnectCount;
    private KVMEvent event;

    public TCPConfig(int port, int heartbeatTime, int maxLossConnectCount, KVMEvent event){
        this.port = port;
        this.heartbeatTime = heartbeatTime;
        this.maxLossConnectCount = maxLossConnectCount;
        this.event = event;
    }
    public TCPConfig(int port, int heartbeatTime, int maxLossConnectCount){
        this(port,heartbeatTime,maxLossConnectCount,null);
    }

    public TCPConfig(int port){
        this(port,DEFAULT_HEARTBEAT_TIME,MAX_LOSS_CONNECT_COUNT,null);
    }

    public TCPConfig(int port, KVMEvent event){
        this(port,DEFAULT_HEARTBEAT_TIME,MAX_LOSS_CONNECT_COUNT,event);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(int heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public KVMEvent getEvent() {
        return event;
    }

    public void setEvent(KVMEvent event) {
        this.event = event;
    }
}
