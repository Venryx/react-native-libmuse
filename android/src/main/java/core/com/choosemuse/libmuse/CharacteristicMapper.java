package com.choosemuse.libmuse;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class CharacteristicMapper {
    public static native CharacteristicMapper instance();

    public abstract ArrayList allCharacteristicUuids();

    public abstract CharacteristicId characteristicForUuid(byte[] bArr);

    public abstract byte[] uuidForCharacteristic(CharacteristicId characteristicId);

    CharacteristicMapper() {
    }

    private static final class CppProxy extends CharacteristicMapper {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private final AtomicBoolean destroyed = new AtomicBoolean(false);
        private final long nativeRef;

        private native void nativeDestroy(long j);

        private native ArrayList native_allCharacteristicUuids(long j);

        private native CharacteristicId native_characteristicForUuid(long j, byte[] bArr);

        private native byte[] native_uuidForCharacteristic(long j, CharacteristicId characteristicId);

        static {
            Class<CharacteristicMapper> cls = CharacteristicMapper.class;
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

        public byte[] uuidForCharacteristic(CharacteristicId characteristicId) {
            return native_uuidForCharacteristic(this.nativeRef, characteristicId);
        }

        public CharacteristicId characteristicForUuid(byte[] bArr) {
            // v-changed
        	return native_characteristicForUuid(this.nativeRef, bArr);
			/*Log.w("libmuse", "ByteArray:" + bArr);
			try {
				return native_characteristicForUuid(this.nativeRef, bArr);
			} catch (Throwable ex) {
				ex.printStackTrace();
				return CharacteristicId.PPG_Z;
			}*/
        }

        public ArrayList allCharacteristicUuids() {
            return native_allCharacteristicUuids(this.nativeRef);
        }
    }
}
