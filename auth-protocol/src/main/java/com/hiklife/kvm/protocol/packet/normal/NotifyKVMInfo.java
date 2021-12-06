package com.hiklife.kvm.protocol.packet.normal;

import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.protocol.packet.KVMPacket;
import com.hiklife.kvm.protocol.packet.PacketItem;
import com.hiklife.kvm.utils.DataProcessingUtil;
import lombok.Data;

import java.util.List;

@Data
public class NotifyKVMInfo{
    private List<PacketItem> payloadData;
    private KVMInfo kvmDevice;
    public NotifyKVMInfo(KVMPacket packet) throws InvalidKVMMessageException {
        payloadData = packet.getPayloadData();
        kvmDevice = new KVMInfo();
        for(PacketItem item: payloadData) {
            byte[] data = item.getData();
            switch (item.getNo()) {
                case 0x01:
                    kvmDevice.setIp(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x02:
                    kvmDevice.setPort1(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x03:
                    kvmDevice.setPort2(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x04:
                    kvmDevice.setPort3(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x05:
                    kvmDevice.setPort4(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x06:
                    kvmDevice.setPort5(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x07:
                    kvmDevice.setConMode(data[0]);
                    break;
                case 0x08:
                    kvmDevice.setConType(data[0]);
                    break;
                case 0x09:
                    kvmDevice.setDeviceNum(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x0A:
                    kvmDevice.setUserName(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x10:
                    kvmDevice.setDeviceId(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x11:
                    kvmDevice.setDeviceName(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x12:
                    kvmDevice.setDeviceType(data[0]);
                    break;
                case 0x13:
                    kvmDevice.setHardwareVer(DataProcessingUtil.bytesToHexString(data));
                    break;
                case 0x14:
                    kvmDevice.setSoftwareVer(DataProcessingUtil.bytesToHexString(data));
                    break;
                case 0x15:
                    kvmDevice.setCascade(data[0]);
                    break;
                case 0x16:
                    kvmDevice.setPortNum(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x17:
                    kvmDevice.setPortState(data);
                    break;
                case 0x18:
                    kvmDevice.setDevicePath(DataProcessingUtil.bytesToAscii(data));
                    break;
                case 0x19:
                    kvmDevice.setUartPort(DataProcessingUtil.registersToInt(data));
                    break;
                case 0x1A:
                    kvmDevice.setUartType(data[0]);
                    break;
                default:
                    throw new InvalidKVMMessageException("KVMInfoItem");
            }
        }
    }
}
