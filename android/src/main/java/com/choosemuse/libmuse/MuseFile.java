package com.choosemuse.libmuse;

public abstract class MuseFile {
    public abstract boolean close(boolean z);

    public abstract boolean open(boolean z);

    public abstract byte[] read(int i);

    public abstract boolean write(byte[] bArr);
}
