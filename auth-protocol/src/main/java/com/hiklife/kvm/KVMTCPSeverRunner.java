package com.hiklife.kvm;

import com.hiklife.kvm.protocol.packet.normal.KVMInfo;
import com.hiklife.kvm.net.TCPConfig;
import com.hiklife.kvm.net.TCPServer;
import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KVMTCPSeverRunner implements TCPSeverRunner{
    private final TCPServer server;

    public KVMTCPSeverRunner(TCPConfig config){
        server = new TCPServer(config);
        KVMEventSingleton.getInstance().setEvent(config.getEvent());
    }
    @Override
    public void startServer(){
        server.startServer();
    }
    @Override
    public void stopServer(){
        server.stopServer();
    }

    @Override
    public void writeRequestTest(KVMInfo device) throws InvalidKVMMessageException {

    }


}
