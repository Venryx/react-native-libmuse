package com.choosemuse.libmuse;

public abstract class MuseDataListener {
    public abstract void receiveMuseArtifactPacket(MuseArtifactPacket museArtifactPacket, Muse muse);

    public abstract void receiveMuseDataPacket(MuseDataPacket museDataPacket, Muse muse);
}
