package com.choosemuse.libmuse;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class RestrictedFeatures {
    public static final String ENABLE_MUSE = "ENABLE_MUSE";
    public static final String ENABLE_SMITHX = "ENABLE_SMITHX";
    public static final String STORE_DATA_WHEN_OFFLINE = "STREAM_DATA_WHEN_OFFLINE";
    public static final String STREAM_BASIC_DATA = "STREAM_BASIC_DATA";
    public static final String STREAM_BIOMETRIC_DATA = "STREAM_BIOMETRIC_DATA";
    public static final String STREAM_DATA_TO_CLOUD = "STREAM_DATA_TO_CLOUD";
    public static final String STREAM_SIGNAL_QUALITY_DATA = "STREAM_SIGNAL_QUALITY_DATA";
    public static final String STREAM_SMITHX_FOCUS_SENSORS = "STREAM_SMITHX_FOCUS_SENSORS";

    public static native RestrictedFeatures getInstance();

    public abstract boolean isEnabled(String str);

    public abstract void populateEnabledFeatures(ArrayList arrayList);

    RestrictedFeatures() {
    }

    private static final class CppProxy extends RestrictedFeatures {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native boolean native_isEnabled(long j, String str);

        private native void native_populateEnabledFeatures(long j, ArrayList arrayList);

        static {
            Class<RestrictedFeatures> cls = RestrictedFeatures.class;
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

        public void populateEnabledFeatures(ArrayList arrayList) {
            native_populateEnabledFeatures(this.nativeRef, arrayList);
        }

        public boolean isEnabled(String str) {
            return native_isEnabled(this.nativeRef, str);
        }
    }
}
