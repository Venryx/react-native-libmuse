package com.choosemuse.libmuse;

public abstract class MuseConnectionListener {
    public abstract void receiveMuseConnectionPacket(MuseConnectionPacket museConnectionPacket, Muse muse);
}
