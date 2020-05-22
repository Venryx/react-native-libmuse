package com.choosemuse.libmuse;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ReaderMuseBuilder {
    public static native ReaderMuseBuilder get();

    public abstract ReaderMuse build(MuseFileReader museFileReader);

    public abstract ReaderMuse buildWithAsync(MuseFileReader museFileReader, EventLoop eventLoop);

    public abstract ReaderMuseBuilder skipPacketTypes(HashSet hashSet);

    public abstract ReaderMuseBuilder withEventLoop(EventLoop eventLoop);

    public abstract ReaderMuseBuilder withModel(MuseModel museModel);

    public abstract ReaderMuseBuilder withPacketTypes(HashSet hashSet);

    public abstract ReaderMuseBuilder withPlaybackSettings(ReaderMusePlaybackSettings readerMusePlaybackSettings);

    private static final class CppProxy extends ReaderMuseBuilder {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native ReaderMuse native_build(long j, MuseFileReader museFileReader);

        private native ReaderMuse native_buildWithAsync(long j, MuseFileReader museFileReader, EventLoop eventLoop);

        private native ReaderMuseBuilder native_skipPacketTypes(long j, HashSet hashSet);

        private native ReaderMuseBuilder native_withEventLoop(long j, EventLoop eventLoop);

        private native ReaderMuseBuilder native_withModel(long j, MuseModel museModel);

        private native ReaderMuseBuilder native_withPacketTypes(long j, HashSet hashSet);

        private native ReaderMuseBuilder native_withPlaybackSettings(long j, ReaderMusePlaybackSettings readerMusePlaybackSettings);

        static {
            Class<ReaderMuseBuilder> cls = ReaderMuseBuilder.class;
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

        public ReaderMuseBuilder withPacketTypes(HashSet hashSet) {
            return native_withPacketTypes(this.nativeRef, hashSet);
        }

        public ReaderMuseBuilder skipPacketTypes(HashSet hashSet) {
            return native_skipPacketTypes(this.nativeRef, hashSet);
        }

        public ReaderMuseBuilder withModel(MuseModel museModel) {
            return native_withModel(this.nativeRef, museModel);
        }

        public ReaderMuseBuilder withPlaybackSettings(ReaderMusePlaybackSettings readerMusePlaybackSettings) {
            return native_withPlaybackSettings(this.nativeRef, readerMusePlaybackSettings);
        }

        public ReaderMuseBuilder withEventLoop(EventLoop eventLoop) {
            return native_withEventLoop(this.nativeRef, eventLoop);
        }

        public ReaderMuse build(MuseFileReader museFileReader) {
            return native_build(this.nativeRef, museFileReader);
        }

        public ReaderMuse buildWithAsync(MuseFileReader museFileReader, EventLoop eventLoop) {
            return native_buildWithAsync(this.nativeRef, museFileReader, eventLoop);
        }
    }
}
