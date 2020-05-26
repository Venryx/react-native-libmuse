package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MuseFileWriter {
    public static native MuseFileWriter getFileWriter(MuseFile museFile);

    public abstract void addAnnotation(int i, AnnotationData annotationData);

    public abstract void addAnnotationString(int i, String str);

    public abstract void addArtifactPacket(int i, MuseArtifactPacket museArtifactPacket);

    public abstract void addComputingDeviceConfiguration(int i, ComputingDeviceConfiguration computingDeviceConfiguration);

    public abstract void addConfiguration(int i, MuseConfiguration museConfiguration);

    public abstract void addDataPacket(int i, MuseDataPacket museDataPacket);

    public abstract void addDsp(int i, DspData dspData);

    public abstract void addVersion(int i, MuseVersion museVersion);

    public abstract boolean close();

    public abstract void discardBufferedPackets();

    public abstract boolean flush();

    public abstract int getBufferedMessagesSize();

    public abstract int getBufferredMessagesCount();

    public abstract boolean open();

    public abstract void setTimestamp(long j);

    public abstract void setTimestampMode(TimestampMode timestampMode);

    private static final class CppProxy extends MuseFileWriter {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native void native_addAnnotation(long j, int i, AnnotationData annotationData);

        private native void native_addAnnotationString(long j, int i, String str);

        private native void native_addArtifactPacket(long j, int i, MuseArtifactPacket museArtifactPacket);

        private native void native_addComputingDeviceConfiguration(long j, int i, ComputingDeviceConfiguration computingDeviceConfiguration);

        private native void native_addConfiguration(long j, int i, MuseConfiguration museConfiguration);

        private native void native_addDataPacket(long j, int i, MuseDataPacket museDataPacket);

        private native void native_addDsp(long j, int i, DspData dspData);

        private native void native_addVersion(long j, int i, MuseVersion museVersion);

        private native boolean native_close(long j);

        private native void native_discardBufferedPackets(long j);

        private native boolean native_flush(long j);

        private native int native_getBufferedMessagesSize(long j);

        private native int native_getBufferredMessagesCount(long j);

        private native boolean native_open(long j);

        private native void native_setTimestamp(long j, long j2);

        private native void native_setTimestampMode(long j, TimestampMode timestampMode);

        static {
            Class<MuseFileWriter> cls = MuseFileWriter.class;
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

        public void discardBufferedPackets() {
            native_discardBufferedPackets(this.nativeRef);
        }

        public boolean flush() {
            return native_flush(this.nativeRef);
        }

        public int getBufferredMessagesCount() {
            return native_getBufferredMessagesCount(this.nativeRef);
        }

        public int getBufferedMessagesSize() {
            return native_getBufferedMessagesSize(this.nativeRef);
        }

        public void addArtifactPacket(int i, MuseArtifactPacket museArtifactPacket) {
            native_addArtifactPacket(this.nativeRef, i, museArtifactPacket);
        }

        public void addDataPacket(int i, MuseDataPacket museDataPacket) {
            native_addDataPacket(this.nativeRef, i, museDataPacket);
        }

        public void addAnnotationString(int i, String str) {
            native_addAnnotationString(this.nativeRef, i, str);
        }

        public void addAnnotation(int i, AnnotationData annotationData) {
            native_addAnnotation(this.nativeRef, i, annotationData);
        }

        public void addConfiguration(int i, MuseConfiguration museConfiguration) {
            native_addConfiguration(this.nativeRef, i, museConfiguration);
        }

        public void addVersion(int i, MuseVersion museVersion) {
            native_addVersion(this.nativeRef, i, museVersion);
        }

        public void addComputingDeviceConfiguration(int i, ComputingDeviceConfiguration computingDeviceConfiguration) {
            native_addComputingDeviceConfiguration(this.nativeRef, i, computingDeviceConfiguration);
        }

        public void addDsp(int i, DspData dspData) {
            native_addDsp(this.nativeRef, i, dspData);
        }

        public void setTimestampMode(TimestampMode timestampMode) {
            native_setTimestampMode(this.nativeRef, timestampMode);
        }

        public void setTimestamp(long j) {
            native_setTimestamp(this.nativeRef, j);
        }
    }
}
