package com.hiklife.kvm.protocol.packet.normal;

import lombok.Data;

@Data
public class KVMCon {
    private String ip;
    private byte conType;
    private String path;
    private String userName;
}
