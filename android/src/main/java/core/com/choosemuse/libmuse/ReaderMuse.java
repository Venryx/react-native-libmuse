package com.choosemuse.libmuse;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ReaderMuse {
    public abstract Muse asMuse();

    public abstract long currentTime();

    public abstract ReaderMusePlaybackSettings getPlaybackSettings();

    public abstract void playback();

    public abstract void run();

    public abstract void runInRealTimespan();

    public abstract void setPlaybackListener(ReaderPlaybackListener readerPlaybackListener);

    public abstract void setPlaybackSettings(ReaderMusePlaybackSettings readerMusePlaybackSettings);

    public abstract void setReaderListener(ReaderListener readerListener);

    private static final class CppProxy extends ReaderMuse {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native Muse native_asMuse(long j);

        private native long native_currentTime(long j);

        private native ReaderMusePlaybackSettings native_getPlaybackSettings(long j);

        private native void native_playback(long j);

        private native void native_run(long j);

        private native void native_runInRealTimespan(long j);

        private native void native_setPlaybackListener(long j, ReaderPlaybackListener readerPlaybackListener);

        private native void native_setPlaybackSettings(long j, ReaderMusePlaybackSettings readerMusePlaybackSettings);

        private native void native_setReaderListener(long j, ReaderListener readerListener);

        static {
            Class<ReaderMuse> cls = ReaderMuse.class;
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

        public void runInRealTimespan() {
            native_runInRealTimespan(this.nativeRef);
        }

        public long currentTime() {
            return native_currentTime(this.nativeRef);
        }

        public void playback() {
            native_playback(this.nativeRef);
        }

        public void setPlaybackSettings(ReaderMusePlaybackSettings readerMusePlaybackSettings) {
            native_setPlaybackSettings(this.nativeRef, readerMusePlaybackSettings);
        }

        public ReaderMusePlaybackSettings getPlaybackSettings() {
            return native_getPlaybackSettings(this.nativeRef);
        }

        public void setReaderListener(ReaderListener readerListener) {
            native_setReaderListener(this.nativeRef, readerListener);
        }

        public void setPlaybackListener(ReaderPlaybackListener readerPlaybackListener) {
            native_setPlaybackListener(this.nativeRef, readerPlaybackListener);
        }

        public Muse asMuse() {
            return native_asMuse(this.nativeRef);
        }
    }
}
