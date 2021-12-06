package com.hiklife.kvm.protocol.packet;

import lombok.Data;

@Data
public class PacketItem {
    public PacketItem(){

    }
    public PacketItem(byte no,int length,byte[] data){
        this.no = no;
        this.length = length;
        this.data = data;
    }
    private byte no;
    private int length;
    private byte[] data;
}
