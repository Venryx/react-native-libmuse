package com.choosemuse.libmuse;

abstract class MusePlatformInterface {
    public abstract void connect();

    public abstract void disconnect();

    public abstract String getAddress();

    public abstract ConnectionHandle getHandle();

    public abstract String getRemoteDeviceName();

    public abstract boolean isConnected();

    MusePlatformInterface() {
    }
}
