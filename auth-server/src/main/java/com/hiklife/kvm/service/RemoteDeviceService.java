package com.hiklife.kvm.service;


import com.hiklife.kvm.KVMEvent;
import com.hiklife.kvm.TCPSeverRunner;
import com.hiklife.kvm.protocol.packet.normal.KVMCon;
import com.hiklife.kvm.protocol.packet.normal.KVMInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
@Slf4j
public class RemoteDeviceService implements KVMEvent {
    @Resource
    private TCPSeverRunner tcpSeverRunner;


    public void startServer(){
        log.info("<startServer>");
        tcpSeverRunner.startServer();
    }

    public void stopServer(){
        log.info("<stopServer>");
        tcpSeverRunner.stopServer();
    }

    @Override
    public void onReadNotifyKVMInfo(KVMInfo kvmInfo) {
        log.debug("<onReadNotifyKVMInfo>:recv KVMInfo");
        //todo
    }

    @Override
    public void onReadNotifyKVMCon(KVMCon kvmCon) {
        log.debug("<onReadNotifyKVMInfo>:recv kvmCon");
        //todo
    }
}
