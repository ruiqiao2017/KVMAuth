package com.hiklife.kvm;

import com.hiklife.kvm.entity.KVMDevice;
import com.hiklife.kvm.net.TCPConfig;
import com.hiklife.kvm.net.TCPServer;

public class TcpServerMain {
    public static void main(String[] args){
        TCPConfig config = new TCPConfig(5900, new KVMEvent() {
            @Override
            public void onReadNotifyKVMInfo(KVMDevice device) {

            }

            @Override
            public void onReadNotifyKVMCon(KVMDevice device) {

            }
        });
        TCPSeverRunner server = new KVMTCPSeverRunner(config);
        server.startServer();
    }
}
