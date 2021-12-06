package com.hiklife.kvm;

public class KVMEventSingleton {
    private static volatile KVMEventSingleton instance = null;

    private KVMEventSingleton() {
    }

    public static KVMEventSingleton getInstance() {
        if (instance == null) {
            synchronized (KVMEventSingleton.class) {
                if (instance == null) {
                    instance = new KVMEventSingleton();
                }
            }
        }
        return instance;
    }

    private KVMEvent event;

    public KVMEvent getEvent() {
        return event;
    }

    public void setEvent(KVMEvent event) {
        this.event = event;
    }
}
