package com.hiklife.kvm;

import com.hiklife.kvm.protocol.exceptions.InvalidKVMMessageException;
import com.hiklife.kvm.protocol.packet.KVMPacket;
import com.hiklife.kvm.protocol.packet.normal.NotifyKVMCon;
import com.hiklife.kvm.protocol.packet.normal.NotifyKVMInfo;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KVMSession implements ReadHandler, WriteHandler {
    private final Channel channel;
    public KVMSession(Channel channel) {
        this.channel = channel;
    }

    public void handle(KVMPacket packet) throws InvalidKVMMessageException {
        switch (packet.getCmd()) {
            case MSG_NOTIFY_KVM_INFO:
                readNotifyKVMInfo(packet);
                break;
            case MSG_NOTIFY_KVM_CON:
                readNotifyKVMCon(packet);
                break;
            default:
        }
    }
    @Override
    public void readNotifyKVMInfo(KVMPacket packet) throws InvalidKVMMessageException {
        NotifyKVMInfo notifyKVMInfo = new NotifyKVMInfo(packet);
        log.debug("<readNotifyKVMInfo>: 收到KVM信息。");
        KVMEventSingleton.getInstance().getEvent().onReadNotifyKVMInfo(notifyKVMInfo.getKvmDevice());
    }

    @Override
    public void readNotifyKVMCon(KVMPacket packet) throws InvalidKVMMessageException {
        NotifyKVMCon notifyKVMCon = new NotifyKVMCon(packet);
        log.debug("<readNotifyKVMCon>: 收到KVM连接信息。");
        KVMEventSingleton.getInstance().getEvent().onReadNotifyKVMCon(notifyKVMCon.getKvmCon());
    }

    @Override
    public void writeResponseTest() {

    }

    private void write(WritePacket packet) {
        channel.writeAndFlush(packet);
    }
    private void closeSession(){
        channel.close();
    }


}
