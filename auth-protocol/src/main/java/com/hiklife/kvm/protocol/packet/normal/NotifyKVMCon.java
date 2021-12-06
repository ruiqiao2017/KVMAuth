package com.hiklife.kvm.protocol.packet.normal;

import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.protocol.packet.KVMPacket;
import com.hiklife.kvm.protocol.packet.PacketItem;
import com.hiklife.kvm.utils.DataProcessingUtil;
import lombok.Data;

import java.util.List;
@Data
public class NotifyKVMCon {
    private List<PacketItem> payloadData;
    private KVMCon kvmCon;
    public NotifyKVMCon(KVMPacket packet) throws InvalidKVMMessageException {
        payloadData = packet.getPayloadData();
        for(PacketItem item: payloadData){
            byte[] data = item.getData();
            switch (item.getNo()){
                case 0x01:
                    kvmCon.setIp(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x02:
                    kvmCon.setConType(data[0]);
                    break;
                case 0x03:
                    int pathLength = data[0];
                    kvmCon.setPath(DataProcessingUtil.bytesToAscii(data,1,pathLength));
                    break;
                case 0x04:
                    kvmCon.setUserName(DataProcessingUtil.bytesToAscii(data));
                    break;
                default:
                    throw new InvalidKVMMessageException("NotifyKVMCon");

            }
        }
    }
}
