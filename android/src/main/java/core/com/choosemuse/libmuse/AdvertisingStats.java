package com.choosemuse.libmuse;

public final class AdvertisingStats {
    final double avgAdvertisingInterval;
    final boolean hasBadMac;
    final boolean isLost;
    final double maxAdvertisingInterval;
    final int numAdvertisingPackets;
    final double sigmaAdvertisingInterval;

    public AdvertisingStats(int i, double d, double d2, double d3, boolean z, boolean z2) {
        this.numAdvertisingPackets = i;
        this.avgAdvertisingInterval = d;
        this.sigmaAdvertisingInterval = d2;
        this.maxAdvertisingInterval = d3;
        this.isLost = z;
        this.hasBadMac = z2;
    }

    public int getNumAdvertisingPackets() {
        return this.numAdvertisingPackets;
    }

    public double getAvgAdvertisingInterval() {
        return this.avgAdvertisingInterval;
    }

    public double getSigmaAdvertisingInterval() {
        return this.sigmaAdvertisingInterval;
    }

    public double getMaxAdvertisingInterval() {
        return this.maxAdvertisingInterval;
    }

    public boolean getIsLost() {
        return this.isLost;
    }

    public boolean getHasBadMac() {
        return this.hasBadMac;
    }

    public String toString() {
        return "AdvertisingStats{numAdvertisingPackets=" + this.numAdvertisingPackets + ",avgAdvertisingInterval=" + this.avgAdvertisingInterval + ",sigmaAdvertisingInterval=" + this.sigmaAdvertisingInterval + ",maxAdvertisingInterval=" + this.maxAdvertisingInterval + ",isLost=" + this.isLost + ",hasBadMac=" + this.hasBadMac + "}";
    }
}
