package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Stringify {
    public static native Stringify instance();

    public abstract String connectionState(ConnectionState connectionState);

    public abstract String packetType(MuseDataPacketType museDataPacketType);

    private static final class CppProxy extends Stringify {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native String native_connectionState(long j, ConnectionState connectionState);

        private native String native_packetType(long j, MuseDataPacketType museDataPacketType);

        static {
            Class<Stringify> cls = Stringify.class;
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

        public String packetType(MuseDataPacketType museDataPacketType) {
            return native_packetType(this.nativeRef, museDataPacketType);
        }

        public String connectionState(ConnectionState connectionState) {
            return native_connectionState(this.nativeRef, connectionState);
        }
    }
}
