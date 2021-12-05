package com.hiklife.kvm;


import com.hiklife.kvm.net.TCPServer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class KVMTCPSeverRunner implements TCPSeverRunner{
    private static final Logger log = LoggerFactory.getLogger(KVMTCPSeverRunner.class);
    private final TCPServer server;

    public KVMTCPSeverRunner(TCPConfig config){
        server = new TCPServer(config);
        DeviceSession.setHikBotEvent(config.getEvent());
    }
    @Override
    public void startServer(){
        server.startServer();
    }
    @Override
    public void stopServer(){
        server.stopServer();
    }

    @Override
    public void writeRequestCalibrationTime(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestCalibrationTime();
            log.debug("<writeRequestCalibrationTime>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceMode(Device device)throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession != null){
            deviceSession.writeRequestDeviceMode(device.getMode().getCode());
            log.debug("<writeRequestDeviceMode>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceName(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceName(device.getName());
            log.debug("<writeRequestDeviceName>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceEvent(Device device,int code,String dateString,String cycle,int sequence) throws InvalidHikBotMessageException{
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession != null){
            deviceSession.writeRequestEvent(code,dateString,cycle,sequence);
            log.debug("<writeRequestDeviceEvent>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceShutDown(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceShutDown();
            log.debug("<writeRequestDeviceShutDown>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceRestart(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceRestart();
            log.debug("<WriteRequestDeviceRestart>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceRestore(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceRestore();
            log.debug("<WriteRequestDeviceRestore>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceModeAcquire(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceModeAcquire();
            log.debug("<writeRequestDeviceModeAcquire>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }

    }

    @Override
    public void writeRequestDeviceFirmwareVersion(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceVersion();
            log.debug("<writeRequestDeviceFirmwareVersion>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }

    }

    @Override
    public void writeRequestDeviceData(Device device, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceData(code);
            log.debug("<writeRequestDeviceData>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }

    }

    @Override
    public void writeRequestDeviceRealVideo(Device device) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestRealVideo();
            log.debug("<writeRequestDeviceRealVideo>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestAudioUploadStart(Device device,int code,int sequence) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestAudioUploadStart(code,sequence);
            log.debug("<writeRequestAudioUploadStart>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestAudioUploadTransfer(Device device, FileData fileData) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestAudioUploadTransfer(fileData);
            log.debug("<writeRequestAudioUploadTransfer>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestAudioUploadStop(Device device,int code,int sequence) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestAudioUploadStop(code,sequence);
            log.debug("<writeRequestAudioUploadStop>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestFirmwareUploadStart(Device device, String md5, String version) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestFirmwareUploadStart(md5,version);
            log.debug("<writeRequestFirmwareUploadStart>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestFirmwareUploadTransfer(Device device, FileData fileData) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestFirmwareUploadTransfer(fileData);
            log.debug("<writeRequestFirmwareUploadTransfer>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestFirmwareUploadStop(Device device, String md5, String version) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestFirmwareUploadStop(md5,version);
            log.debug("<writeRequestFirmwareUploadStop>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestEventSetting(Device device,int code, int sequence) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestEventSetting(code,sequence);
            log.debug("<writeRequestEventSetting>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestVolumeSetting(Device device, int value) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestVolumeSetting(value);
            log.debug("<writeRequestVolumeSetting>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestMuteSetting(Device device, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestMuteSetting(code);
            log.debug("<writeRequestMuteSetting>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestThresholdSetting(Device device, int code, int value) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestThresholdSetting(code,value);
            log.debug("<writeRequestThresholdSetting>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestCameraSwitch(Device device, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestCameraSwitch(code);
            log.debug("<writeRequestTCameraSwitch>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestCameraPTZControl(Device device, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestCameraPTZControl(code);
            log.debug("<writeRequestTCameraPTZControl>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeRequestDeviceBLNControl(Device device, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(device.getSn());
        if(deviceSession!=null){
            deviceSession.writeRequestDeviceBLNControl(code);
            log.debug("<writeRequestCameraSwitch>:{}", device.getSn());
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }

    @Override
    public void writeNotifyAlarmEvent(String sn, int code) throws InvalidHikBotMessageException {
        DeviceSession deviceSession =TCPServerHandler.deviceSessionMap.get(sn);
        if(deviceSession!=null){
            deviceSession.writeNotifyAlarmEvent(code);
            log.debug("<writeRequestCameraSwitch>:{}", sn);
        }else{
            throw new InvalidHikBotMessageException("设备连接失败");
        }
    }
}
