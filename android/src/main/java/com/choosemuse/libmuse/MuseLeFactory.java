package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class MuseLeFactory {
    public static native Muse getMuse(MuseLeDevice museLeDevice, EventLoop eventLoop);

    MuseLeFactory() {
    }

    private static final class CppProxy extends MuseLeFactory {
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
