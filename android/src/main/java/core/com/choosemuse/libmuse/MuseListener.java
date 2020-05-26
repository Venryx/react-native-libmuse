package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseListener {
    public abstract void museListChanged();

    private static final class CppProxy extends MuseListener {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native void native_museListChanged(long j);

        static {
            Class<MuseListener> cls = MuseListener.class;
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

        public void museListChanged() {
            native_museListChanged(this.nativeRef);
        }
    }
}
