package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LogManager {
    public static native LogManager instance();

    public abstract long getTimestamp();

    public abstract LogListener makeDefaultLogListener();

    public abstract void setLogListener(LogListener logListener);

    public abstract void setMinimumSeverity(Severity severity);

    public abstract double timeSince(long j);

    public abstract void writeLog(Severity severity, boolean z, String str, String str2);

    private static final class CppProxy extends LogManager {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native long native_getTimestamp(long j);

        private native LogListener native_makeDefaultLogListener(long j);

        private native void native_setLogListener(long j, LogListener logListener);

        private native void native_setMinimumSeverity(long j, Severity severity);

        private native double native_timeSince(long j, long j2);

        private native void native_writeLog(long j, Severity severity, boolean z, String str, String str2);

        static {
            Class<LogManager> cls = LogManager.class;
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

        public LogListener makeDefaultLogListener() {
            return native_makeDefaultLogListener(this.nativeRef);
        }

        public void setLogListener(LogListener logListener) {
            native_setLogListener(this.nativeRef, logListener);
        }

        public void setMinimumSeverity(Severity severity) {
            native_setMinimumSeverity(this.nativeRef, severity);
        }

        public void writeLog(Severity severity, boolean z, String str, String str2) {
            native_writeLog(this.nativeRef, severity, z, str, str2);
        }

        public long getTimestamp() {
            return native_getTimestamp(this.nativeRef);
        }

        public double timeSince(long j) {
            return native_timeSince(this.nativeRef, j);
        }
    }
}
