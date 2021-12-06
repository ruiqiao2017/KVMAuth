package com.hiklife.kvm;

import com.hiklife.kvm.protocol.packet.normal.KVMCon;
import com.hiklife.kvm.protocol.packet.normal.KVMInfo;
import com.hiklife.kvm.net.TCPConfig;

public class TcpServerMain {
    public static void main(String[] args){
        TCPConfig config = new TCPConfig(5900, new KVMEvent() {
            @Override
            public void onReadNotifyKVMInfo(KVMInfo kvmInfo) {
                System.out.println(kvmInfo);
            }

            @Override
            public void onReadNotifyKVMCon(KVMCon kvmCon) {
                System.out.println(kvmCon);
            }
        });
        TCPSeverRunner server = new KVMTCPSeverRunner(config);
        server.startServer();
    }
}
