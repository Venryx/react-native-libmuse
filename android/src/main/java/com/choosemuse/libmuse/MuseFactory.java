package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class MuseFactory {
    public static native Muse getMuse(MusePlatformInterface musePlatformInterface, EventLoop eventLoop);

    MuseFactory() {
    }

    private static final class CppProxy extends MuseFactory {
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

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
    }
}
