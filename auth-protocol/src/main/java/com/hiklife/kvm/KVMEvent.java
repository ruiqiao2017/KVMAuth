package com.hiklife.kvm;

/**
 * 协议通知事件
 */
public interface KVMEvent {
    HikBotResponsePacket onAuthDevice(Device device);
    void onDeviceOnline(Device device);
    void onDeviceOffline(Device device);
    void onDeviceStatus(Device device);
    void onDeviceTrouble(Device device, DeviceTrouble deviceTrouble);
    void onDeviceAlarmEvent(Device device, DeviceAlarmEvent deviceAlarmEvent);
    void onDeviceData(Device device, DeviceData deviceData, int value);
    void onEventStatus(Device device, int sequence, EventStatus eventStatus);
    void onDeviceDecibel(Device device, int decibelValue, String timestamp);
    FileData onRequestVideoUploadStart(Device device);
    void onRequestVideoUploadStop(Device device,FileData data);
    void onDeviceCalibrationTime(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceEvent(Device device,HikBotResponsePacket hikBotResponsePacket);
    void onDeviceEventSetting(Device device, int sequence, EventStatus eventStatus,HikBotResponsePacket hikBotResponsePacket);
    void onDeviceName(Device device,HikBotResponsePacket hikBotResponsePacket);
    void onDeviceMode(Device device,HikBotResponsePacket hikBotResponsePacket);
    void onDeviceRealVideo(Device device,HikBotResponsePacket hikBotResponsePacket);
    void onDeviceShutdown(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceRestart(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceRestore(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceModeAcquire(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceFirmwareVersion(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceDataAcquire(Device device, DeviceData deviceData, int value ,HikBotResponsePacket hikBotResponsePacket);
    void onAudioFile(Device device, HikBotResponsePacket hikBotResponsePacket,int status);
    void onFirmwareFile(Device device, HikBotResponsePacket hikBotResponsePacket,int status);
    void onDeviceVolumeSetting(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceMuteSetting(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceThresholdSetting(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceCameraSwitch(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceCameraPTZControl(Device device, HikBotResponsePacket hikBotResponsePacket);
    void onDeviceDeviceBLNControl(Device device, HikBotResponsePacket hikBotResponsePacket);
}
