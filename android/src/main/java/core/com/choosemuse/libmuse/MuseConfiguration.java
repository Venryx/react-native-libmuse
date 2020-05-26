package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseConfiguration {
    public abstract int getAccelerometerSampleFrequency();

    public abstract int getAdcFrequency();

    public abstract int getAfeGain();

    public abstract boolean getBatteryDataEnabled();

    public abstract double getBatteryPercentRemaining();

    public abstract String getBluetoothMac();

    public abstract int getDownsampleRate();

    public abstract boolean getDrlRefEnabled();

    public abstract int getDrlRefFrequency();

    public abstract int getEegChannelCount();

    public abstract String getHeadbandName();

    public abstract String getMicrocontrollerId();

    public abstract MuseModel getModel();

    public abstract NotchFrequency getNotchFilter();

    public abstract boolean getNotchFilterEnabled();

    public abstract int getOutputFrequency();

    public abstract MusePreset getPreset();

    public abstract String getSerialNumber();

    public abstract int getSeroutMode();

    private static final class CppProxy extends MuseConfiguration {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native int native_getAccelerometerSampleFrequency(long j);

        private native int native_getAdcFrequency(long j);

        private native int native_getAfeGain(long j);

        private native boolean native_getBatteryDataEnabled(long j);

        private native double native_getBatteryPercentRemaining(long j);

        private native String native_getBluetoothMac(long j);

        private native int native_getDownsampleRate(long j);

        private native boolean native_getDrlRefEnabled(long j);

        private native int native_getDrlRefFrequency(long j);

        private native int native_getEegChannelCount(long j);

        private native String native_getHeadbandName(long j);

        private native String native_getMicrocontrollerId(long j);

        private native MuseModel native_getModel(long j);

        private native NotchFrequency native_getNotchFilter(long j);

        private native boolean native_getNotchFilterEnabled(long j);

        private native int native_getOutputFrequency(long j);

        private native MusePreset native_getPreset(long j);

        private native String native_getSerialNumber(long j);

        private native int native_getSeroutMode(long j);

        static {
            Class<MuseConfiguration> cls = MuseConfiguration.class;
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

        public MusePreset getPreset() {
            return native_getPreset(this.nativeRef);
        }

        public String getHeadbandName() {
            return native_getHeadbandName(this.nativeRef);
        }

        public String getMicrocontrollerId() {
            return native_getMicrocontrollerId(this.nativeRef);
        }

        public int getEegChannelCount() {
            return native_getEegChannelCount(this.nativeRef);
        }

        public int getAfeGain() {
            return native_getAfeGain(this.nativeRef);
        }

        public int getDownsampleRate() {
            return native_getDownsampleRate(this.nativeRef);
        }

        public int getSeroutMode() {
            return native_getSeroutMode(this.nativeRef);
        }

        public int getOutputFrequency() {
            return native_getOutputFrequency(this.nativeRef);
        }

        public int getAdcFrequency() {
            return native_getAdcFrequency(this.nativeRef);
        }

        public boolean getNotchFilterEnabled() {
            return native_getNotchFilterEnabled(this.nativeRef);
        }

        public NotchFrequency getNotchFilter() {
            return native_getNotchFilter(this.nativeRef);
        }

        public int getAccelerometerSampleFrequency() {
            return native_getAccelerometerSampleFrequency(this.nativeRef);
        }

        public boolean getBatteryDataEnabled() {
            return native_getBatteryDataEnabled(this.nativeRef);
        }

        public boolean getDrlRefEnabled() {
            return native_getDrlRefEnabled(this.nativeRef);
        }

        public int getDrlRefFrequency() {
            return native_getDrlRefFrequency(this.nativeRef);
        }

        public double getBatteryPercentRemaining() {
            return native_getBatteryPercentRemaining(this.nativeRef);
        }

        public String getBluetoothMac() {
            return native_getBluetoothMac(this.nativeRef);
        }

        public String getSerialNumber() {
            return native_getSerialNumber(this.nativeRef);
        }

        public MuseModel getModel() {
            return native_getModel(this.nativeRef);
        }
    }
}
