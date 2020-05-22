package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Action {
    public abstract void run();

    private static final class CppProxy extends Action {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native void native_run(long j);

        static {
            Class<Action> cls = Action.class;
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

        public void run() {
            native_run(this.nativeRef);
        }
    }
}
