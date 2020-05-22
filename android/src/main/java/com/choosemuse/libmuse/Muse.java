package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Muse {
    public abstract void connect();

    public abstract void disconnect();

    public abstract void enableDataTransmission(boolean z);

    public abstract void enableException(boolean z);

    public abstract void execute();

    public abstract ConnectionState getConnectionState();

    public abstract double getLastDiscoveredTime();

    public abstract String getMacAddress();

    public abstract MuseConfiguration getMuseConfiguration();

    public abstract MuseVersion getMuseVersion();

    public abstract String getName();

    public abstract double getRssi();

    public abstract boolean isLowEnergy();

    public abstract boolean isPaired();

    public abstract void registerConnectionListener(MuseConnectionListener museConnectionListener);

    public abstract void registerDataListener(MuseDataListener museDataListener, MuseDataPacketType museDataPacketType);

    public abstract void registerErrorListener(MuseErrorListener museErrorListener);

    public abstract void runAsynchronously();

    public abstract void setNotchFrequency(NotchFrequency notchFrequency);

    public abstract void setNumConnectTries(int i);

    public abstract void setPreset(MusePreset musePreset);

    public abstract void unregisterAllListeners();

    public abstract void unregisterConnectionListener(MuseConnectionListener museConnectionListener);

    public abstract void unregisterDataListener(MuseDataListener museDataListener, MuseDataPacketType museDataPacketType);

    public abstract void unregisterErrorListener(MuseErrorListener museErrorListener);

    private static final class CppProxy extends Muse {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native void native_connect(long j);

        private native void native_disconnect(long j);

        private native void native_enableDataTransmission(long j, boolean z);

        private native void native_enableException(long j, boolean z);

        private native void native_execute(long j);

        private native ConnectionState native_getConnectionState(long j);

        private native double native_getLastDiscoveredTime(long j);

        private native String native_getMacAddress(long j);

        private native MuseConfiguration native_getMuseConfiguration(long j);

        private native MuseVersion native_getMuseVersion(long j);

        private native String native_getName(long j);

        private native double native_getRssi(long j);

        private native boolean native_isLowEnergy(long j);

        private native boolean native_isPaired(long j);

        private native void native_registerConnectionListener(long j, MuseConnectionListener museConnectionListener);

        private native void native_registerDataListener(long j, MuseDataListener museDataListener, MuseDataPacketType museDataPacketType);

        private native void native_registerErrorListener(long j, MuseErrorListener museErrorListener);

        private native void native_runAsynchronously(long j);

        private native void native_setNotchFrequency(long j, NotchFrequency notchFrequency);

        private native void native_setNumConnectTries(long j, int i);

        private native void native_setPreset(long j, MusePreset musePreset);

        private native void native_unregisterAllListeners(long j);

        private native void native_unregisterConnectionListener(long j, MuseConnectionListener museConnectionListener);

        private native void native_unregisterDataListener(long j, MuseDataListener museDataListener, MuseDataPacketType museDataPacketType);

        private native void native_unregisterErrorListener(long j, MuseErrorListener museErrorListener);

        static {
            Class<Muse> cls = Muse.class;
        }

        private CppProxy(long j) {
            if (j != 0) {
                this.nativeRef = j;
                return;
            }
            throw new RuntimeException("nativeRef is zero");
        }

        public void destroy() {
            if (!this.destroyed.getAndSet(true)) {
                nativeDestroy(this.nativeRef);
            }
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            destroy();
            super.finalize();
        }

        public void connect() {
            native_connect(this.nativeRef);
        }

        public void disconnect() {
            native_disconnect(this.nativeRef);
        }

        public void execute() {
            native_execute(this.nativeRef);
        }

        public void runAsynchronously() {
            native_runAsynchronously(this.nativeRef);
        }

        public ConnectionState getConnectionState() {
            return native_getConnectionState(this.nativeRef);
        }

        public String getMacAddress() {
            return native_getMacAddress(this.nativeRef);
        }

        public String getName() {
            return native_getName(this.nativeRef);
        }

        public double getRssi() {
            return native_getRssi(this.nativeRef);
        }

        public double getLastDiscoveredTime() {
            return native_getLastDiscoveredTime(this.nativeRef);
        }

        public void setNumConnectTries(int i) {
            native_setNumConnectTries(this.nativeRef, i);
        }

        public MuseConfiguration getMuseConfiguration() {
            return native_getMuseConfiguration(this.nativeRef);
        }

        public MuseVersion getMuseVersion() {
            return native_getMuseVersion(this.nativeRef);
        }

        public void registerConnectionListener(MuseConnectionListener museConnectionListener) {
            native_registerConnectionListener(this.nativeRef, museConnectionListener);
        }

        public void unregisterConnectionListener(MuseConnectionListener museConnectionListener) {
            native_unregisterConnectionListener(this.nativeRef, museConnectionListener);
        }

        public void registerDataListener(MuseDataListener museDataListener, MuseDataPacketType museDataPacketType) {
            native_registerDataListener(this.nativeRef, museDataListener, museDataPacketType);
        }

        public void unregisterDataListener(MuseDataListener museDataListener, MuseDataPacketType museDataPacketType) {
            native_unregisterDataListener(this.nativeRef, museDataListener, museDataPacketType);
        }

        public void registerErrorListener(MuseErrorListener museErrorListener) {
            native_registerErrorListener(this.nativeRef, museErrorListener);
        }

        public void unregisterErrorListener(MuseErrorListener museErrorListener) {
            native_unregisterErrorListener(this.nativeRef, museErrorListener);
        }

        public void unregisterAllListeners() {
            native_unregisterAllListeners(this.nativeRef);
        }

        public void setPreset(MusePreset musePreset) {
            native_setPreset(this.nativeRef, musePreset);
        }

        public void enableDataTransmission(boolean z) {
            native_enableDataTransmission(this.nativeRef, z);
        }

        public void setNotchFrequency(NotchFrequency notchFrequency) {
            native_setNotchFrequency(this.nativeRef, notchFrequency);
        }

        public boolean isLowEnergy() {
            return native_isLowEnergy(this.nativeRef);
        }

        public boolean isPaired() {
            return native_isPaired(this.nativeRef);
        }

        public void enableException(boolean z) {
            native_enableException(this.nativeRef, z);
        }
    }
}
