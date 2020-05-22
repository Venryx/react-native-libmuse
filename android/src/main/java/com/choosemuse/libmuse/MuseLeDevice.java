package com.choosemuse.libmuse;

abstract class MuseLeDevice {
    public abstract DeviceInformation getInfo();

    public abstract int getOsLevel();

    public abstract boolean isPaired();

    public abstract void requestConnect();

    public abstract void requestDisconnect();

    public abstract void requestStartReceiving(CharacteristicId characteristicId);

    public abstract void requestStopReceiving(CharacteristicId characteristicId);

    public abstract void setDelegate(MuseLeDeviceDelegate museLeDeviceDelegate);

    public abstract void writeValueToCharacteristic(CharacteristicId characteristicId, byte[] bArr);

    MuseLeDevice() {
    }
}
