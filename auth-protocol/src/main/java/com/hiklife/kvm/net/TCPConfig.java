package com.hiklife.kvm.net;

import com.hiklife.kvm.KVMEvent;
import lombok.Data;

@Data
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

}
