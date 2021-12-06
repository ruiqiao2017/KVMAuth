package com.hiklife.kvm.protocol.packet.normal;

import lombok.Data;

@Data
public class KVMInfo {
    private String ip;
    private int port1;
    private int port2;
    private int port3;
    private int port4;
    private int port5;
    private byte conMode;
    private byte conType;
    private int deviceNum;
    private String userName;
    private String deviceId;
    private String deviceName;
    private byte deviceType;
    private String hardwareVer;
    private String softwareVer;
    private byte cascade;
    private int portNum;
    private byte[] portState;
    private String devicePath;
    private int uartPort;
    private byte uartType;
}
