package com.hiklife.kvm;

public interface WriteHandler {
    /**
     * server--device
     * 心跳应答
     */
    void writeResponseHeartBeat(int uuid);

    /**
     * server--device
     * 设备校验应道
     */
    void writeResponseAuth(int uuid, HikBotResponsePacket packet);
    /**
     * server--device
     * 录像上传开始应答
     */
    void writeResponseVideoUploadStart(int uuid, HikBotResponsePacket packet);
    /**
     * server--device
     * 录像上传结束应答
     */
    void writeResponseVideoUploadStop(int uuid, HikBotResponsePacket packet);
    /**
     * server--device
     * 主动发起：配置设备时间
     */
    void writeRequestCalibrationTime();

    /**
     * server--device
     * 主动发起：设备名称修改
     */
    void writeRequestDeviceName(String name);

    /**
     * server--device
     * 主动发起：设备模式修改
     */
    void writeRequestDeviceMode(int code);

    /**
     * server--device
     * 主动发起：事件预约
     */
    void writeRequestEvent(int code,String dateString,String cycle,int sequence);

    /**
     * server--device
     * 主动发起：事件状态设置
     */
    void writeRequestEventSetting(int code,int sequence);

    /**
     * server--device
     * 主动发起：视频播放url
     */
    void writeRequestRealVideo();

    /**
     * server--device
     * 主动发起：设备模式获取
     */
    void writeRequestDeviceModeAcquire();

    /**
     * server--device
     * 主动发起：设备版本号获取
     */
    void writeRequestDeviceVersion();

    /**
     * server--device
     * 主动发起：获取设备数据
     */
    void writeRequestDeviceData(int code);

    /**
     * server--device
     * 主动发起：设备关机
     */
    void writeRequestDeviceShutDown();

    /**
     * server--device
     * 主动发起：设备重启
     */
    void writeRequestDeviceRestart();

    /**
     * server--device
     * 主动发起：设备恢复出厂设置
     */
    void writeRequestDeviceRestore();

    /**
     * server--device
     * 主动发起：自定义音频下发开始
     */
    void writeRequestAudioUploadStart(int code,int sequence);

    /**
     * server--device
     * 主动发起：自定义音频下发结束
     */
    void writeRequestAudioUploadStop(int code,int sequence);

    /**
     * server--device
     * 主动发起：自定义音频传输
     */
    void writeRequestAudioUploadTransfer(FileData fileData);

    /**
     * server--device
     * 主动发起：固件下发开始
     */
    void writeRequestFirmwareUploadStart(String md5, String version);

    /**
     * server--device
     * 主动发起：固件下发结束
     */
    void writeRequestFirmwareUploadStop(String md5, String version);

    /**
     * server--device
     * 主动发起：固件内容传输
     */
    void writeRequestFirmwareUploadTransfer(FileData fileData);

    /**
     * server--device
     * 主动发起：音量调节
     */
    void writeRequestVolumeSetting(int value);

    /**
     * server--device
     * 主动发起：静音开关
     */
    void writeRequestMuteSetting(int code);

    /**
     * server--device
     * 主动发起：设备参数阈值
     */
    void writeRequestThresholdSetting(int code, int value);

    /**
     * server--device
     * 主动发起：摄像头开关
     */
    void writeRequestCameraSwitch(int code);

    /**
     * server--device
     * 主动发起：摄像头云台控制
     */
    void writeRequestCameraPTZControl(int code);

    /**
     * server--device
     * 主动发起：呼吸灯控制
     */
    void writeRequestDeviceBLNControl(int code);

    /**
     * server--device
     * 主动发起：异常行为事件通知
     */
    void writeNotifyAlarmEvent(int code);

}
