package com.hiklife.kvm.config;


import com.hiklife.kvm.KVMTCPSeverRunner;
import com.hiklife.kvm.TCPSeverRunner;
import com.hiklife.kvm.net.TCPConfig;
import com.hiklife.kvm.service.RemoteDeviceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;

@Configuration
public class BeanConfig {
    @Resource
    private RemoteDeviceService remoteDeviceService;

    @Value("${tcpserver.port}")
    private int tcpPort;
    @Value("${tcpserver..heartbeat-time}")
    private int heartbeatTime;
    @Value("${tcpserver..heartbeat-time}")
    private int maxLossConnectCount;
    @Bean
    @Primary
    public TCPSeverRunner initTCPServer() {
        return new KVMTCPSeverRunner(new TCPConfig(tcpPort, heartbeatTime, maxLossConnectCount,remoteDeviceService));
    }
}
