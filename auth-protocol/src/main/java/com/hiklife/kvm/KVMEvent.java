package com.hiklife.kvm;

import com.hiklife.kvm.entity.KVMDevice;

/**
 * 协议通知事件
 */
public interface KVMEvent {
    void onReadNotifyKVMInfo(KVMDevice device);
    void onReadNotifyKVMCon(KVMDevice device);
}
