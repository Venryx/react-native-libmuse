package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ApiVersion {
    public abstract long getApi();

    public abstract long getMajor();

    public abstract long getMinor();

    public abstract long getMonotonic();

    public abstract long getPatch();

    public abstract String getString();

    private static final class CppProxy extends ApiVersion {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native long native_getApi(long j);

        private native long native_getMajor(long j);

        private native long native_getMinor(long j);

        private native long native_getMonotonic(long j);

        private native long native_getPatch(long j);

        private native String native_getString(long j);

        static {
            Class<ApiVersion> cls = ApiVersion.class;
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

        public long getMonotonic() {
            return native_getMonotonic(this.nativeRef);
        }

        public long getMajor() {
            return native_getMajor(this.nativeRef);
        }

        public long getMinor() {
            return native_getMinor(this.nativeRef);
        }

        public long getPatch() {
            return native_getPatch(this.nativeRef);
        }

        public long getApi() {
            return native_getApi(this.nativeRef);
        }

        public String getString() {
            return native_getString(this.nativeRef);
        }
    }
}
