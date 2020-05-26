package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseFileReader {
    public static native MuseFileReader getFileReader(MuseFile museFile);

    public abstract boolean close();

    public abstract AnnotationData getAnnotation();

    public abstract MuseArtifactPacket getArtifactPacket();

    public abstract ComputingDeviceConfiguration getComputingDeviceConfiguration();

    public abstract MuseConfiguration getConfiguration();

    public abstract MuseDataPacket getDataPacket();

    public abstract DspData getDsp();

    public abstract int getMessageId();

    public abstract long getMessageTimestamp();

    public abstract MessageType getMessageType();

    public abstract MuseVersion getVersion();

    public abstract Result gotoNextMessage();

    public abstract boolean open();

    private static final class CppProxy extends MuseFileReader {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native boolean native_close(long j);

        private native AnnotationData native_getAnnotation(long j);

        private native MuseArtifactPacket native_getArtifactPacket(long j);

        private native ComputingDeviceConfiguration native_getComputingDeviceConfiguration(long j);

        private native MuseConfiguration native_getConfiguration(long j);

        private native MuseDataPacket native_getDataPacket(long j);

        private native DspData native_getDsp(long j);

        private native int native_getMessageId(long j);

        private native long native_getMessageTimestamp(long j);

        private native MessageType native_getMessageType(long j);

        private native MuseVersion native_getVersion(long j);

        private native Result native_gotoNextMessage(long j);

        private native boolean native_open(long j);

        static {
            Class<MuseFileReader> cls = MuseFileReader.class;
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

        public boolean open() {
            return native_open(this.nativeRef);
        }

        public boolean close() {
            return native_close(this.nativeRef);
        }

        public Result gotoNextMessage() {
            return native_gotoNextMessage(this.nativeRef);
        }

        public MessageType getMessageType() {
            return native_getMessageType(this.nativeRef);
        }

        public int getMessageId() {
            return native_getMessageId(this.nativeRef);
        }

        public long getMessageTimestamp() {
            return native_getMessageTimestamp(this.nativeRef);
        }

        public AnnotationData getAnnotation() {
            return native_getAnnotation(this.nativeRef);
        }

        public MuseConfiguration getConfiguration() {
            return native_getConfiguration(this.nativeRef);
        }

        public MuseVersion getVersion() {
            return native_getVersion(this.nativeRef);
        }

        public ComputingDeviceConfiguration getComputingDeviceConfiguration() {
            return native_getComputingDeviceConfiguration(this.nativeRef);
        }

        public DspData getDsp() {
            return native_getDsp(this.nativeRef);
        }

        public MuseDataPacket getDataPacket() {
            return native_getDataPacket(this.nativeRef);
        }

        public MuseArtifactPacket getArtifactPacket() {
            return native_getArtifactPacket(this.nativeRef);
        }
    }
}
