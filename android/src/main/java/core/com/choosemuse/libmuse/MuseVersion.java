package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseVersion {
    public static native MuseVersion makeDefaultVersion();

    public static native MuseVersion makeVersion(String str);

    public abstract String getBootloaderVersion();

    public abstract String getBspVersion();

    public abstract String getFirmwareBuildNumber();

    public abstract String getFirmwareType();

    public abstract String getFirmwareVersion();

    public abstract String getHardwareVersion();

    public abstract int getProtocolVersion();

    public abstract String getRunningState();

    private static final class CppProxy extends MuseVersion {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native String native_getBootloaderVersion(long j);

        private native String native_getBspVersion(long j);

        private native String native_getFirmwareBuildNumber(long j);

        private native String native_getFirmwareType(long j);

        private native String native_getFirmwareVersion(long j);

        private native String native_getHardwareVersion(long j);

        private native int native_getProtocolVersion(long j);

        private native String native_getRunningState(long j);

        static {
            Class<MuseVersion> cls = MuseVersion.class;
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

        public String getRunningState() {
            return native_getRunningState(this.nativeRef);
        }

        public String getHardwareVersion() {
            return native_getHardwareVersion(this.nativeRef);
        }

        public String getBspVersion() {
            return native_getBspVersion(this.nativeRef);
        }

        public String getFirmwareVersion() {
            return native_getFirmwareVersion(this.nativeRef);
        }

        public String getBootloaderVersion() {
            return native_getBootloaderVersion(this.nativeRef);
        }

        public String getFirmwareBuildNumber() {
            return native_getFirmwareBuildNumber(this.nativeRef);
        }

        public String getFirmwareType() {
            return native_getFirmwareType(this.nativeRef);
        }

        public int getProtocolVersion() {
            return native_getProtocolVersion(this.nativeRef);
        }
    }
}
