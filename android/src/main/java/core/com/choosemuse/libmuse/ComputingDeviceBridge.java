package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class ComputingDeviceBridge {
    public static native ComputingDeviceBridge getInstance();

    public abstract ComputingDeviceConfiguration getComputingDeviceConfig();

    public abstract String getRecorderKey();

    public abstract String getRecorderName();

    public abstract String getRecorderVersion();

    public abstract void setComputingDeviceInterface(ComputingDeviceInterface computingDeviceInterface);

    ComputingDeviceBridge() {
    }

    private static final class CppProxy extends ComputingDeviceBridge {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native ComputingDeviceConfiguration native_getComputingDeviceConfig(long j);

        private native String native_getRecorderKey(long j);

        private native String native_getRecorderName(long j);

        private native String native_getRecorderVersion(long j);

        private native void native_setComputingDeviceInterface(long j, ComputingDeviceInterface computingDeviceInterface);

        static {
            Class<ComputingDeviceBridge> cls = ComputingDeviceBridge.class;
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

        public void setComputingDeviceInterface(ComputingDeviceInterface computingDeviceInterface) {
            native_setComputingDeviceInterface(this.nativeRef, computingDeviceInterface);
        }

        public ComputingDeviceConfiguration getComputingDeviceConfig() {
            return native_getComputingDeviceConfig(this.nativeRef);
        }

        public String getRecorderName() {
            return native_getRecorderName(this.nativeRef);
        }

        public String getRecorderVersion() {
            return native_getRecorderVersion(this.nativeRef);
        }

        public String getRecorderKey() {
            return native_getRecorderKey(this.nativeRef);
        }
    }
}
