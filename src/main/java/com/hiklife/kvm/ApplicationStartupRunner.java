package com.hiklife.kvm;

import com.hiklife.kvm.service.RemoteDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class ApplicationStartupRunner implements ApplicationRunner {
    @Resource
    private RemoteDeviceService remoteDeviceService;
    @Override
    public void run(ApplicationArguments args) {
        log.debug("<run>: TCPServer starting");
        remoteDeviceService.startServer();
    }
}
