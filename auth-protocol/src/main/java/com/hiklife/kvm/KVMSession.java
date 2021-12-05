package com.hiklife.kvm;


import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;

public class KVMSession implements ReadHandler, WriteHandler {
    private static final Logger log = LoggerFactory.getLogger(KVMSession.class);
    private final Channel channel;
    private KVMProtocolSate protocolSate;
    private static KVMEvent event;
    private Device device;
    private String deviceName;
    private DeviceMode deviceMode;
    private DeviceEvent deviceEvent;
    private int appointmentSettingSequence;
    private EventStatus appointmentStatusCode;
    private boolean duplication = false;
    private FileData   fileData = new FileData(Constant.FILE_TRANSFER_STATUS_NOT_READY);
    public KVMSession(Channel channel) {
        protocolSate = HikBotProtocolSate.AUTH;
        this.channel = channel;
    }

    public static void setHikBotEvent(HikBotEvent event) {
        hikBotEvent = event;
    }

    public void handle(HikBotPacket packet) throws InvalidHikBotMessageException {
        switch (protocolSate) {
            case AUTH:
                readRequestAuth(packet);
                break;
            case NORMAL:
                readNormal(packet);
                break;
            default:
        }
    }

    public void readNormal(HikBotPacket packet) throws InvalidHikBotMessageException {
        short tag = packet.getHeader().getTag();
        HikBotPacketTag hikBotMsgType = HikBotPacketTag.parse(tag);
        switch (hikBotMsgType) {
            case MSG_REQ_HEART_BEAT:
                readRequestHeartBeat(packet);
                break;
            case MSG_NOTIFY_DEVICE_STATUS:
                readNotifyDeviceStatus(packet);
                break;
            case MSG_NOTIFY_DEVICE_ALARM_EVENT:
                readNotifyDeviceAlarmEvent(packet);
                break;
            case MSG_NOTIFY_DEVICE_TROUBLE:
                readNotifyDeviceTrouble(packet);
                break;
            case MSG_NOTIFY_DEVICE_DATA:
                readNotifyDeviceData(packet);
                break;
            case MSG_NOTIFY_EVENT_STATUS:
                readNotifyEventStatus(packet);
                break;
            case MSG_NOTIFY_DEVICE_DECIBEL:
                readNotifyDeviceDecibel(packet);
                break;
            case MSG_REQ_VIDEO_UPLOAD_START:
                readRequestVideoUploadStart(packet);
                break;
            case MSG_REQ_VIDEO_UPLOAD_STOP:
                readRequestVideoUploadStop(packet);
                break;
            case MSG_REQ_VIDEO_UPLOAD_TRANSFER:
                readRequestVideoUploadTransfer(packet);
                break;
            case MSG_RSP_AUDIO_UPLOAD_START:
                readResponseAudioUploadStart(packet);
                break;
            case MSG_RSP_AUDIO_UPLOAD_STOP:
                readResponseAudioUploadStop(packet);
                break;
            case MSG_RSP_FIRMWARE_UPLOAD_START:
                readResponseFirmwareUploadStart(packet);
                break;
            case MSG_RSP_FIRMWARE_UPLOAD_STOP:
                readResponseFirmwareUploadStop(packet);
                break;
            case MSG_RSP_DEVICE_NAME:
                readResponseDeviceName(packet);
                break;
            case MSG_RSP_EVENT:
                readResponseDeviceEvent(packet);
                break;
            case MSG_RSP_MODE:
                readResponseDeviceMode(packet);
                break;
            case MSG_RSP_TIMING:
                readResponseCalibrationTime(packet);
                break;
            case MSG_RSP_REAL_VIDE:
                readResponseRealVideo(packet);
                break;
            case MSG_RSP_SHUT_DOWN:
                readResponseDeviceShutDown(packet);
                break;
            case MSG_RSP_RESTART:
                readResponseDeviceRestart(packet);
                break;
            case MSG_RSP_RESTORE:
                readResponseDeviceRestore(packet);
                break;
            case MSG_RSP_DEVICE_MODE_ACQUIRE:
                readResponseDeviceModeAcquire(packet);
                break;
            case MSG_RSP_DEVICE_DATA:
                readResponseDeviceData(packet);
                break;
            case MSG_RSP_DEVICE_FIRMWARE_VERSION:
                readResponseDeviceVersion(packet);
                break;
            case MSG_RSP_EVENT_SETTING:
                readResponseEventSetting(packet);
                break;
            case MSG_RSP_VOLUME_SETTING:
                readResponseVolumeSetting(packet);
                break;
            case MSG_RSP_MUTE_SETTING:
                readResponseMuteSetting(packet);
                break;
            case MSG_RSP_THRESHOLD_SETTING:
                readResponseThresholdSetting(packet);
                break;
            case MSG_RSP_CAMERA_SWITCH:
                readResponseCameraSwitch(packet);
                break;
            case MSG_RSP_PTZ_CONTROL:
                readResponseCameraPTZControl(packet);
                break;
            case MSG_RSP_BLN_CONTROL:
                readResponseDeviceBLNControl(packet);
                break;
            case NONE:
            default:
                log.error("<handle>: Can not find the hikBotMsgType,{}",
                        DataProcessingUtil.bytesToHexString(DataProcessingUtil.intToRegisters(tag)));
                break;
        }
    }

    @Override
    public void readRequestAuth(HikBotPacket packet) throws InvalidHikBotMessageException {
        short tag = packet.getHeader().getTag();
        if(HikBotPacketTag.parse(tag) != HikBotPacketTag.MSG_REQ_AUTH) {
            log.error("<readRequestAuth>: Can not parse the MSG_REQ_AUTH packet, ignore it. tag:{}",
                    DataProcessingUtil.bytesToHexString(DataProcessingUtil.intToRegisters(tag)));
            return;
        }
        ReadRequestAuth readRequestAuth = new ReadRequestAuth(packet);
        HikBotResponsePacket responsePacket = new HikBotResponsePacket();
        device = readRequestAuth.getDevice();
        InetSocketAddress socketAddress = (InetSocketAddress)channel.remoteAddress();
        device.setIp(socketAddress.getAddress().getHostAddress());
        /*
         * 协议版本认证
         */
        if(!Version.greaterThanOrEqual(Version.VERSION_CURRENT, readRequestAuth.getVersion())){
            responsePacket.setCode(HikBotResponsePacket.RESPONSE_CODE_FAIL);
            responsePacket.setMsg("protocol version auth failed");
            writeResponseAuth(packet.getHeader().getUuid(), responsePacket);
            log.info("<readRequestAuth>: 协议版本认证失败");
            closeSession();
            return;
        }

        /*
         * 设备认证
         */
        responsePacket = hikBotEvent.onAuthDevice(device);
        if(responsePacket.getCode()==HikBotResponsePacket.RESPONSE_CODE_SUCCESS){
            DeviceSession session = TCPServerHandler.deviceSessionMap.put(device.getSn(),this);
            if(session!=null){
                log.error("发现设备sn {} 新连接，关闭原来连接",device.getSn());
                session.setDuplication(true);
                session.closeSession();
            }
        }else{
            log.info("<readRequestAuth>: 设备SN码认证失败");
            responsePacket.setMsg("sn auth failed");
            writeResponseAuth(packet.getHeader().getUuid(), responsePacket);
            closeSession();
            return;
        }
        writeResponseAuth(packet.getHeader().getUuid(), responsePacket);
        protocolSate = HikBotProtocolSate.NORMAL;
        device.setOnline(DeviceOnline.ONLINE);
        hikBotEvent.onDeviceOnline(readRequestAuth.getDevice());
        writeRequestCalibrationTime();
        writeRequestRealVideo();
        writeRequestDeviceVersion();
    }

    @Override
    public void readRequestHeartBeat(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadRequestHeartBeat readRequestHeartBeat = new ReadRequestHeartBeat(packet);
        log.debug("<readRequestHeartBeat>: 收到心跳信息，sn, {}", readRequestHeartBeat.getSn());
        writeResponseHeartBeat(packet.getHeader().getUuid());
    }

    @Override
    public void readNotifyDeviceStatus(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyDeviceStatus deviceStatus = new ReadNotifyDeviceStatus(packet);
        device.setMode(deviceStatus.getMode());
        hikBotEvent.onDeviceStatus(device);
    }

    @Override
    public void readNotifyDeviceTrouble(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyDeviceTrouble deviceTrouble = new ReadNotifyDeviceTrouble(packet);
        hikBotEvent.onDeviceTrouble(device,deviceTrouble.getTrouble());
    }

    @Override
    public void readNotifyDeviceAlarmEvent(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyDeviceAlarmEvent deviceAlarmEvent = new ReadNotifyDeviceAlarmEvent(packet);
        hikBotEvent.onDeviceAlarmEvent(device, deviceAlarmEvent.getAlarmEvent());
    }

    @Override
    public void readNotifyDeviceData(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyDeviceData deviceData = new ReadNotifyDeviceData(packet);
        hikBotEvent.onDeviceData(device,deviceData.getData(),deviceData.getDataValue());
    }

    @Override
    public void readNotifyEventStatus(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyEventStatus eventStatus = new ReadNotifyEventStatus(packet);
        hikBotEvent.onEventStatus(device,eventStatus.getSequence(),eventStatus.getEventStatus());
    }

    @Override
    public void readNotifyDeviceDecibel(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadNotifyDeviceDecibel decibel = new ReadNotifyDeviceDecibel(packet);
        hikBotEvent.onDeviceDecibel(device,decibel.getDecibelValue(),decibel.getTimestamp());
    }

    @Override
    public void readRequestVideoUploadStart(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadRequestVideoUploadStart readRequestVideoUploadStart = new ReadRequestVideoUploadStart(packet);
        if(fileData.getStatus()!=Constant.FILE_TRANSFER_STATUS_NOT_READY){
            log.error("<readRequestVideoUploadStart>: 该设备当前有视频文件正在传输中，请稍后，sn, {}", device.getSn());
            return;
        }
        log.debug("<readRequestVideoUploadStart>: 收到视频上传请求，sn, {}", device.getSn());
        fileData = new FileData();
        fileData = hikBotEvent.onRequestVideoUploadStart(device);
        fileData.setVideoType(readRequestVideoUploadStart.getVideoType());
        HikBotResponsePacket responsePacket = new HikBotResponsePacket();
        responsePacket.setCode(HikBotResponsePacket.RESPONSE_CODE_SUCCESS);
        writeResponseVideoUploadStart(packet.getHeader().getUuid(),responsePacket);
        fileData.setStatus(Constant.FILE_TRANSFER_STATUS_PROCESS);
    }

    @Override
    public void readRequestVideoUploadStop(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadRequestVideoUploadStop readRequestVideoUploadStop = new ReadRequestVideoUploadStop(packet);
        fileData.setStatus(Constant.FILE_TRANSFER_STATUS_STOP);
        log.debug("<readRequestVideoUploadStop>: 收到视频结束请求，sn, {}", device.getSn());
        hikBotEvent.onRequestVideoUploadStop(device,fileData);
        HikBotResponsePacket responsePacket = new HikBotResponsePacket();
        responsePacket.setCode(HikBotResponsePacket.RESPONSE_CODE_SUCCESS);
        writeResponseVideoUploadStop(packet.getHeader().getUuid(),responsePacket);
        fileData.setStatus(Constant.FILE_TRANSFER_STATUS_NOT_READY);
    }

    @Override
    public void readRequestVideoUploadTransfer(HikBotPacket packet) throws InvalidHikBotMessageException {
        log.debug("<readRequestVideoUploadStop>: 收到视频传输内容，sn, {}", device.getSn());
        ReadRequestVideoUploadTransfer readRequestVideoUploadTransfer = new ReadRequestVideoUploadTransfer(packet);
        if(fileData.getStatus()!=Constant.FILE_TRANSFER_STATUS_PROCESS){
            log.debug("<readRequestVideoUploadTransfer>: 当前状态错误，无法接受视频数据，sn, {}", device.getSn());
        }
        fileData.setData(readRequestVideoUploadTransfer.getContent());
        fileData.setBeginIndex(readRequestVideoUploadTransfer.getPosition());
        try {
             FileUtils.writeFile(fileData);
        } catch (IOException e) {
            log.error("<readRequestVideoUploadTransfer>: 文件写入错误，{}，sn, {}", e.getMessage(),device.getSn());
            fileData.setStatus(Constant.FILE_TRANSFER_STATUS_NOT_READY);
        }
    }


    @Override
    public void readResponseAudioUploadStart(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseAudioUploadStart readResponseAudioUploadStart = new ReadResponseAudioUploadStart(packet);
        hikBotEvent.onAudioFile(device, new HikBotResponsePacket((byte) readResponseAudioUploadStart.getCode(),readResponseAudioUploadStart.getResult()),Constant.FILE_TRANSFER_STATUS_START);
    }

    @Override
    public void readResponseAudioUploadStop(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseAudioUploadStop readResponseAudioUploadStop = new ReadResponseAudioUploadStop(packet);
        hikBotEvent.onAudioFile(device, new HikBotResponsePacket((byte) readResponseAudioUploadStop.getCode(),readResponseAudioUploadStop.getResult()),Constant.FILE_TRANSFER_STATUS_STOP);
    }

    @Override
    public void readResponseFirmwareUploadStart(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseFirmwareUploadStart readResponseFirmwareUploadStart = new ReadResponseFirmwareUploadStart(packet);
        hikBotEvent.onFirmwareFile(device, new HikBotResponsePacket((byte) readResponseFirmwareUploadStart.getCode(),readResponseFirmwareUploadStart.getResult()),Constant.FILE_TRANSFER_STATUS_START);
    }

    @Override
    public void readResponseFirmwareUploadStop(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseFirmwareUploadStop readResponseFirmwareUploadStop = new ReadResponseFirmwareUploadStop(packet);
        hikBotEvent.onFirmwareFile(device, new HikBotResponsePacket((byte) readResponseFirmwareUploadStop.getCode(),readResponseFirmwareUploadStop.getResult()),Constant.FILE_TRANSFER_STATUS_STOP);
    }


    @Override
    public void readResponseCalibrationTime(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseCalibrationTime calibrationTime = new ReadResponseCalibrationTime(packet);
        hikBotEvent.onDeviceCalibrationTime(device, new HikBotResponsePacket((byte)calibrationTime.getCode(), calibrationTime.getResult()));
    }

    @Override
    public void readResponseDeviceName(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceName deviceName = new ReadResponseDeviceName(packet);
        if(deviceName.getCode()==0){
            device.setName(this.deviceName);
        }
        hikBotEvent.onDeviceName(device, new HikBotResponsePacket((byte)deviceName.getCode(), deviceName.getResult()));

    }

    @Override
    public void readResponseDeviceEvent(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceEvent deviceEvent = new ReadResponseDeviceEvent(packet);
        hikBotEvent.onDeviceEvent(device, new HikBotResponsePacket((byte)deviceEvent.getCode(), deviceEvent.getResult()));
    }

    @Override
    public void readResponseEventSetting(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseEventStatus eventStatus = new ReadResponseEventStatus(packet);
        hikBotEvent.onDeviceEventSetting(device, appointmentSettingSequence,appointmentStatusCode,new HikBotResponsePacket((byte)eventStatus.getCode(), eventStatus.getResult()));
    }

    @Override
    public void readResponseDeviceMode(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceMode deviceMode = new ReadResponseDeviceMode(packet);
        if(deviceMode.getCode()==0){
            device.setMode(this.deviceMode);
        }
        hikBotEvent.onDeviceMode(device, new HikBotResponsePacket((byte)deviceMode.getCode(), deviceMode.getResult()));
    }

    @Override
    public void readResponseDeviceShutDown(HikBotPacket packet) {
        hikBotEvent.onDeviceShutdown(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS,null));
    }

    @Override
    public void readResponseDeviceRestart(HikBotPacket packet)  {
        hikBotEvent.onDeviceRestart(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS,null));
    }

    @Override
    public void readResponseDeviceRestore(HikBotPacket packet)  {
        hikBotEvent.onDeviceRestore(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS,null));
    }

    @Override
    public void readResponseDeviceModeAcquire(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceModeAcquire readResponseDeviceModeAcquire = new ReadResponseDeviceModeAcquire(packet);
        device.setMode(readResponseDeviceModeAcquire.getMode());
        hikBotEvent.onDeviceModeAcquire(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS, device.getMode().toString()));

    }

    @Override
    public void readResponseDeviceData(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceData readResponseDeviceData = new ReadResponseDeviceData(packet);
        hikBotEvent.onDeviceDataAcquire(device, readResponseDeviceData.getData(),readResponseDeviceData.getDataValue(), new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS,String.valueOf(readResponseDeviceData.getDataValue())));

    }

    @Override
    public void readResponseDeviceVersion(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceVersion readResponseDeviceVersion = new ReadResponseDeviceVersion(packet);
        hikBotEvent.onDeviceFirmwareVersion(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS,readResponseDeviceVersion.getResult()));

    }

    @Override
    public void readResponseRealVideo(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceRealVideo realVideo = new ReadResponseDeviceRealVideo(packet);
        device.setRtsp(realVideo.getResult());
        hikBotEvent.onDeviceRealVideo(device, new HikBotResponsePacket(HikBotResponsePacket.RESPONSE_CODE_SUCCESS, realVideo.getResult()));
    }

    @Override
    public void readResponseVolumeSetting(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseVolumeSetting volumeSetting = new ReadResponseVolumeSetting(packet);
        hikBotEvent.onDeviceVolumeSetting(device, new HikBotResponsePacket((byte)volumeSetting.getCode(), volumeSetting.getResult()));
    }

    @Override
    public void readResponseMuteSetting(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseMuteSetting muteSetting = new ReadResponseMuteSetting(packet);
        hikBotEvent.onDeviceMuteSetting(device, new HikBotResponsePacket((byte)muteSetting.getCode(), muteSetting.getResult()));
    }

    @Override
    public void readResponseThresholdSetting(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseThresholdSetting thresholdSetting = new ReadResponseThresholdSetting(packet);
        hikBotEvent.onDeviceThresholdSetting(device, new HikBotResponsePacket((byte)thresholdSetting.getCode(), thresholdSetting.getResult()));
    }

    @Override
    public void readResponseCameraSwitch(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseCameraSwitch cameraSwitch = new ReadResponseCameraSwitch(packet);
        hikBotEvent.onDeviceCameraSwitch(device, new HikBotResponsePacket((byte)cameraSwitch.getCode(), cameraSwitch.getResult()));
    }

    @Override
    public void readResponseCameraPTZControl(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseCameraPTZControl cameraPTZControl = new ReadResponseCameraPTZControl(packet);
        hikBotEvent.onDeviceCameraPTZControl(device, new HikBotResponsePacket((byte)cameraPTZControl.getCode(), cameraPTZControl.getResult()));
    }

    @Override
    public void readResponseDeviceBLNControl(HikBotPacket packet) throws InvalidHikBotMessageException {
        ReadResponseDeviceBLNControl deviceBLNControl = new ReadResponseDeviceBLNControl(packet);
        hikBotEvent.onDeviceDeviceBLNControl(device, new HikBotResponsePacket((byte)deviceBLNControl.getCode(), deviceBLNControl.getResult()));
    }

    @Override
    public void writeResponseHeartBeat(int uuid) {
        WriteResponseHearBeat writeResponseHearBeat = new WriteResponseHearBeat(uuid);
        write(writeResponseHearBeat);
    }

    @Override
    public void writeResponseAuth(int uuid, HikBotResponsePacket rpsPacket) {
        WriteResponseAuth writeResponseAuth = new WriteResponseAuth(uuid, new HikBotWritePacket(rpsPacket));
        write(writeResponseAuth);
    }

    @Override
    public void writeResponseVideoUploadStart(int uuid, HikBotResponsePacket packet) {
        WriteResponseVideoUploadStart writeResponseVideoUploadStart = new WriteResponseVideoUploadStart(uuid, new HikBotWritePacket(packet));
        write(writeResponseVideoUploadStart);
    }

    @Override
    public void writeResponseVideoUploadStop(int uuid, HikBotResponsePacket packet) {
        WriteResponseVideoUploadStop writeResponseVideoUploadStop = new WriteResponseVideoUploadStop(uuid, new HikBotWritePacket(packet));
        write(writeResponseVideoUploadStop);
    }

    @Override
    public void writeRequestCalibrationTime(){
        String dateString = DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss");
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode(),dateString,-1);
        WriteRequestCalibrationTime writeRequestCalibrationTime = new WriteRequestCalibrationTime(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestCalibrationTime);
    }

    @Override
    public void writeRequestDeviceName(String name) {
        deviceName=name;
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_DEVICE_NAME.getCode(),deviceName,-1,Constant.REQUEST_PACKET_TYPE_DEVICE_NAME);
        WriteRequestDeviceName writeRequestDeviceName = new WriteRequestDeviceName(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceName);
    }

    @Override
    public void writeRequestDeviceMode(int code) {
        deviceMode=DeviceMode.parse(code);
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,deviceMode.getCode());
        WriteRequestDeviceMode writeRequestDeviceMode = new WriteRequestDeviceMode(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceMode);
    }

    @Override
    public void writeRequestEvent(int code,String dateString,String cycle,int sequence) {
        deviceEvent=DeviceEvent.parse(code);
        int cycleInt = DataProcessingUtil.scale2Decimal(cycle,2);
        HikBotRequestPacket requestPacketEvent = new HikBotRequestPacket(
                new short[]{HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),HikBotPacketConstantTag.CONSTANT_CASE_SEQUENCE.getCode(), HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode(),HikBotPacketConstantTag.CONSTANT_EVENT_CLOCK.getCode()},
                new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED, dateString,Constant.MSG_UNDEFINED},new int[]{deviceEvent.getCode(),sequence,-1,cycleInt});
        WriteRequestDeviceEvent writeRequestDeviceEvent = new WriteRequestDeviceEvent(Sequence.nextValue(),new HikBotWritePacket(requestPacketEvent));
        write(writeRequestDeviceEvent);
    }

    @Override
    public void writeRequestEventSetting(int code, int sequence) {
        appointmentSettingSequence=sequence;
        appointmentStatusCode=EventStatus.parse(code);
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_CASE_SEQUENCE.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode()},new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{sequence,code});
        WriteRequestEventStatus writeRequestEventStatus = new WriteRequestEventStatus(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestEventStatus);
    }

    @Override
    public void writeRequestRealVideo() {
        WriteRequestDeviceRealVideo writeRequestDeviceRealVideo = new WriteRequestDeviceRealVideo(Sequence.nextValue());
        write(writeRequestDeviceRealVideo);
    }

    @Override
    public void writeRequestDeviceModeAcquire() {
        WriteRequestDeviceModeAcquire writeRequestDeviceModeAcquire = new WriteRequestDeviceModeAcquire(Sequence.nextValue());
        write(writeRequestDeviceModeAcquire);
    }

    @Override
    public void writeRequestDeviceVersion() {
        WriteRequestDeviceVersion writeRequestDeviceVersion = new WriteRequestDeviceVersion(Sequence.nextValue());
        write(writeRequestDeviceVersion);
    }

    @Override
    public void writeRequestDeviceData(int code) {
        DeviceData deviceData = DeviceData.parse(code);
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,deviceData.getCode());
        WriteRequestDeviceData writeRequestDeviceData = new WriteRequestDeviceData(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceData);

    }

    @Override
    public void writeRequestDeviceShutDown() {
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_DEVICE_SN.getCode(),device.getSn(),-1);
        WriteRequestDeviceShutDown writeRequestDeviceShutDown = new WriteRequestDeviceShutDown(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceShutDown);
    }

    @Override
    public void writeRequestDeviceRestart() {
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_DEVICE_SN.getCode(),device.getSn(),-1);
        WriteRequestDeviceRestart writeRequestDeviceRestart = new WriteRequestDeviceRestart(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceRestart );
    }

    @Override
    public void writeRequestDeviceRestore() {
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_DEVICE_SN.getCode(),device.getSn(),-1);
        WriteRequestDeviceRestore writeRequestDeviceRestore = new WriteRequestDeviceRestore(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceRestore);
    }

    @Override
    public void writeRequestAudioUploadStart(int code,int sequence) {
        deviceEvent=DeviceEvent.parse(code);
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_CASE_SEQUENCE.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode()}, new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{sequence,deviceEvent.getCode()});
        WriteRequestAudioUploadStart writeRequestAudioUploadStart = new WriteRequestAudioUploadStart(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestAudioUploadStart);
    }

    @Override
    public void writeRequestAudioUploadStop(int code,int sequence) {
        deviceEvent=DeviceEvent.parse(code);
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_CASE_SEQUENCE.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode()}, new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{sequence,deviceEvent.getCode()});
        WriteRequestAudioUploadStop writeRequestAudioUploadStop = new WriteRequestAudioUploadStop(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestAudioUploadStop);
    }

    @Override
    public void writeRequestAudioUploadTransfer(FileData fileData) {
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_CASE_SEQUENCE.getCode(),HikBotPacketConstantTag.CONSTANT_FILE_POSITION.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode()}, new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{fileData.getSequence(),fileData.getSendIndex(),-1},fileData.getData(),Constant.REQUEST_PACKET_TYPE_FILE);
        WriteRequestAudioTransfer writeRequestAudioTransfer = new WriteRequestAudioTransfer(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestAudioTransfer);
    }

    @Override
    public void writeRequestFirmwareUploadStart(String md5, String version) {
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode()}, new String[]{version,md5},new int[]{-1,-1});
        WriteRequestFirmwareUploadStart writeRequestFirmwareUploadStart = new WriteRequestFirmwareUploadStart(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestFirmwareUploadStart);
    }

    @Override
    public void writeRequestFirmwareUploadStop(String md5, String version) {
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(), md5,-1);
        WriteRequestFirmwareUploadStop writeRequestFirmwareUploadStop = new WriteRequestFirmwareUploadStop(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestFirmwareUploadStop);
    }

    @Override
    public void writeRequestFirmwareUploadTransfer(FileData fileData) {
        HikBotRequestPacket requestPacket = new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_FILE_POSITION.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode()}, new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{fileData.getSendIndex(),-1},fileData.getData(),Constant.REQUEST_PACKET_TYPE_FIRMWARE);
        WriteRequestFirmwareTransfer writeRequestFirmwareTransfer = new WriteRequestFirmwareTransfer(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestFirmwareTransfer);
    }

    @Override
    public void writeRequestVolumeSetting(int value) {
        device.setVolume(value);
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,value);
        WriteRequestVolumeSetting writeRequestVolumeSetting = new WriteRequestVolumeSetting(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestVolumeSetting);
    }

    @Override
    public void writeRequestMuteSetting(int code) {
        device.setDeviceMute(DeviceMute.parse(code));
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,code);
        WriteRequestMuteSetting writeRequestVolumeSetting = new WriteRequestMuteSetting(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestVolumeSetting);
    }

    @Override
    public void writeRequestThresholdSetting(int code, int value) {
        device.setDeviceThreshold(DeviceThreshold.parse(code));
        device.setThresholdValue(value);
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(new short[]{HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),HikBotPacketConstantTag.CONSTANT_NORMAL_VAL.getCode()},new String[]{Constant.MSG_UNDEFINED,Constant.MSG_UNDEFINED},new int[]{code,value},Constant.REQUEST_PACKET_TYPE_INT_VALUE);
        WriteRequestThresholdSetting writeRequestThresholdSetting = new WriteRequestThresholdSetting(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestThresholdSetting);
    }

    @Override
    public void writeRequestCameraSwitch(int code) {
        device.setCameraStatus(CameraStatus.parse(code));
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,code);
        WriteRequestCameraSwitch writeRequestCameraSwitch = new WriteRequestCameraSwitch(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestCameraSwitch);
    }

    @Override
    public void writeRequestCameraPTZControl(int code) {
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,code);
        WriteRequestCameraPTZControl writeRequestCameraPTZControl = new WriteRequestCameraPTZControl(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestCameraPTZControl);
    }

    @Override
    public void writeRequestDeviceBLNControl(int code) {
        device.setDeviceBLNControl(DeviceBLNControl.parse(code));
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,code);
        WriteRequestDeviceBLNControl writeRequestDeviceBLNControl = new WriteRequestDeviceBLNControl(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeRequestDeviceBLNControl);
    }

    @Override
    public void writeNotifyAlarmEvent(int code) {
        HikBotRequestPacket requestPacket =new HikBotRequestPacket(HikBotPacketConstantTag.CONSTANT_NORMAL_CODE.getCode(),Constant.MSG_UNDEFINED,code);
        WriteNotifyAlarmEvent writeNotifyAlarmEvent = new WriteNotifyAlarmEvent(Sequence.nextValue(),new HikBotWritePacket(requestPacket));
        write(writeNotifyAlarmEvent);
    }

    private void write(WritePacket packet) {
        channel.writeAndFlush(packet);
    }

    private void closeSession(){
        channel.close();
    }

    public void kickDeviceOff(Device device){
        hikBotEvent.onDeviceOffline(device);
    }


    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public boolean isDuplication() {
        return duplication;
    }

    public void setDuplication(boolean duplication) {
        this.duplication = duplication;
    }


}
