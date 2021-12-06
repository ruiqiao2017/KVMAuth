package com.hiklife.kvm;

import com.hiklife.kvm.protocol.packet.normal.KVMCon;
import com.hiklife.kvm.protocol.packet.normal.KVMInfo;

/**
 * 协议通知事件
 */
public interface KVMEvent {
    void onReadNotifyKVMInfo(KVMInfo kvmInfo);
    void onReadNotifyKVMCon(KVMCon kvmCon);
}
