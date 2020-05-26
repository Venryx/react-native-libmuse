package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class MuseLeDeviceDelegate {
    public abstract void didConnect();

    public abstract void didDisconnect();

    public abstract void didReceiveValueForCharacteristic(CharacteristicId characteristicId, byte[] bArr);

    public abstract void didStartReceiving();

    public abstract void didStopReceiving();

    MuseLeDeviceDelegate() {
    }

    private static final class CppProxy extends MuseLeDeviceDelegate {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native void native_didConnect(long j);

        private native void native_didDisconnect(long j);

        private native void native_didReceiveValueForCharacteristic(long j, CharacteristicId characteristicId, byte[] bArr);

        private native void native_didStartReceiving(long j);

        private native void native_didStopReceiving(long j);

        static {
            Class<MuseLeDeviceDelegate> cls = MuseLeDeviceDelegate.class;
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

        public void didConnect() {
            native_didConnect(this.nativeRef);
        }

        public void didDisconnect() {
            native_didDisconnect(this.nativeRef);
        }

        public void didStartReceiving() {
            native_didStartReceiving(this.nativeRef);
        }

        public void didStopReceiving() {
            native_didStopReceiving(this.nativeRef);
        }

        public void didReceiveValueForCharacteristic(CharacteristicId characteristicId, byte[] bArr) {
            native_didReceiveValueForCharacteristic(this.nativeRef, characteristicId, bArr);
        }
    }
}
