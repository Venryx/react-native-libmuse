package com.choosemuse.libmuse;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseDataPacket {
    public static native MuseDataPacket makePacket(MuseDataPacketType museDataPacketType, long j, ArrayList arrayList);

    public static native MuseDataPacket makeUninitializedPacket(long j);

    public abstract double getAccelerometerValue(Accelerometer accelerometer);

    public abstract double getBatteryValue(Battery battery);

    public abstract double getDrlRefValue(DrlRef drlRef);

    public abstract double getEegChannelValue(Eeg eeg);

    public abstract double getGyroValue(Gyro gyro);

    public abstract MuseDataPacketType packetType();

    public abstract long timestamp();

    public abstract ArrayList values();

    public abstract long valuesSize();

    private static final class CppProxy extends MuseDataPacket {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native double native_getAccelerometerValue(long j, Accelerometer accelerometer);

        private native double native_getBatteryValue(long j, Battery battery);

        private native double native_getDrlRefValue(long j, DrlRef drlRef);

        private native double native_getEegChannelValue(long j, Eeg eeg);

        private native double native_getGyroValue(long j, Gyro gyro);

        private native MuseDataPacketType native_packetType(long j);

        private native long native_timestamp(long j);

        private native ArrayList native_values(long j);

        private native long native_valuesSize(long j);

        static {
            Class<MuseDataPacket> cls = MuseDataPacket.class;
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

        public MuseDataPacketType packetType() {
            return native_packetType(this.nativeRef);
        }

        public long timestamp() {
            return native_timestamp(this.nativeRef);
        }

        public ArrayList values() {
            return native_values(this.nativeRef);
        }

        public long valuesSize() {
            return native_valuesSize(this.nativeRef);
        }

        public double getEegChannelValue(Eeg eeg) {
            return native_getEegChannelValue(this.nativeRef, eeg);
        }

        public double getBatteryValue(Battery battery) {
            return native_getBatteryValue(this.nativeRef, battery);
        }

        public double getAccelerometerValue(Accelerometer accelerometer) {
            return native_getAccelerometerValue(this.nativeRef, accelerometer);
        }

        public double getGyroValue(Gyro gyro) {
            return native_getGyroValue(this.nativeRef, gyro);
        }

        public double getDrlRefValue(DrlRef drlRef) {
            return native_getDrlRefValue(this.nativeRef, drlRef);
        }
    }
}
