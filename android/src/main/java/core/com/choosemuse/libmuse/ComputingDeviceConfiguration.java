package com.choosemuse.libmuse;

public final class ComputingDeviceConfiguration {
    final String bluetoothVersion;
    final String hardwareModelId;
    final String hardwareModelName;
    final String memorySize;
    final int numberOfProcessors;
    final String osType;
    final String osVersion;
    final String processorName;
    final String processorSpeed;
    final String timeZone;
    final int timeZoneOffsetSeconds;

    public ComputingDeviceConfiguration(String str, String str2, String str3, String str4, String str5, String str6, int i, String str7, String str8, String str9, int i2) {
        this.osType = str;
        this.osVersion = str2;
        this.hardwareModelName = str3;
        this.hardwareModelId = str4;
        this.processorName = str5;
        this.processorSpeed = str6;
        this.numberOfProcessors = i;
        this.memorySize = str7;
        this.bluetoothVersion = str8;
        this.timeZone = str9;
        this.timeZoneOffsetSeconds = i2;
    }

    public String getOsType() {
        return this.osType;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public String getHardwareModelName() {
        return this.hardwareModelName;
    }

    public String getHardwareModelId() {
        return this.hardwareModelId;
    }

    public String getProcessorName() {
        return this.processorName;
    }

    public String getProcessorSpeed() {
        return this.processorSpeed;
    }

    public int getNumberOfProcessors() {
        return this.numberOfProcessors;
    }

    public String getMemorySize() {
        return this.memorySize;
    }

    public String getBluetoothVersion() {
        return this.bluetoothVersion;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public int getTimeZoneOffsetSeconds() {
        return this.timeZoneOffsetSeconds;
    }

    public String toString() {
        return "ComputingDeviceConfiguration{osType=" + this.osType + ",osVersion=" + this.osVersion + ",hardwareModelName=" + this.hardwareModelName + ",hardwareModelId=" + this.hardwareModelId + ",processorName=" + this.processorName + ",processorSpeed=" + this.processorSpeed + ",numberOfProcessors=" + this.numberOfProcessors + ",memorySize=" + this.memorySize + ",bluetoothVersion=" + this.bluetoothVersion + ",timeZone=" + this.timeZone + ",timeZoneOffsetSeconds=" + this.timeZoneOffsetSeconds + "}";
    }
}
