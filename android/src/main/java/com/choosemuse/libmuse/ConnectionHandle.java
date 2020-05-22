package com.choosemuse.libmuse;

abstract class ConnectionHandle {
    public abstract byte[] getBytes();

    public abstract boolean hasBytes();

    public abstract boolean writeBinaryBytes(byte[] bArr);

    public abstract boolean writeBytes(String str);

    ConnectionHandle() {
    }
}
