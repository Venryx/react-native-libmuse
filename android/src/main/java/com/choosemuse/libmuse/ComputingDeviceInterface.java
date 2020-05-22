package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class ComputingDeviceInterface {
    public abstract ComputingDeviceConfiguration getComputingDeviceConfig();

    public abstract String getRecorderKey();

    public abstract String getRecorderName();

    public abstract String getRecorderVersion();

    ComputingDeviceInterface() {
    }

    private static final class CppProxy extends ComputingDeviceInterface {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native ComputingDeviceConfiguration native_getComputingDeviceConfig(long j);

        private native String native_getRecorderKey(long j);

        private native String native_getRecorderName(long j);

        private native String native_getRecorderVersion(long j);

        static {
            Class<ComputingDeviceInterface> cls = ComputingDeviceInterface.class;
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

        public String getRecorderName() {
            return native_getRecorderName(this.nativeRef);
        }

        public String getRecorderVersion() {
            return native_getRecorderVersion(this.nativeRef);
        }

        public String getRecorderKey() {
            return native_getRecorderKey(this.nativeRef);
        }

        public ComputingDeviceConfiguration getComputingDeviceConfig() {
            return native_getComputingDeviceConfig(this.nativeRef);
        }
    }
}
