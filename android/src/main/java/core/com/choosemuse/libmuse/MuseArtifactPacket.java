package com.choosemuse.libmuse;

public final class MuseArtifactPacket {
    final boolean blink;
    final boolean headbandOn;
    final boolean jawClench;
    final long timestamp;

    public MuseArtifactPacket(boolean z, boolean z2, boolean z3, long j) {
        this.headbandOn = z;
        this.blink = z2;
        this.jawClench = z3;
        this.timestamp = j;
    }

    public boolean getHeadbandOn() {
        return this.headbandOn;
    }

    public boolean getBlink() {
        return this.blink;
    }

    public boolean getJawClench() {
        return this.jawClench;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String toString() {
        return "MuseArtifactPacket{headbandOn=" + this.headbandOn + ",blink=" + this.blink + ",jawClench=" + this.jawClench + ",timestamp=" + this.timestamp + "}";
    }
}
