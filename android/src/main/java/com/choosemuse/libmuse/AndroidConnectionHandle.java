package com.choosemuse.libmuse;

final class AndroidConnectionHandle extends ConnectionHandle {
    private MusePlatformAndroid platform;

    AndroidConnectionHandle(MusePlatformAndroid musePlatformAndroid) {
        this.platform = musePlatformAndroid;
    }

    public boolean hasBytes() {
        return this.platform.hasBytes();
    }

    public byte[] getBytes() {
        return this.platform.getBytes();
    }

    public boolean writeBytes(String str) {
        return this.platform.writeBinaryBytes(str.getBytes());
    }

    public boolean writeBinaryBytes(byte[] bArr) {
        return this.platform.writeBinaryBytes(bArr);
    }
}
