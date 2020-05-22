package com.choosemuse.libmuse;

public final class DeviceInformation {
    final String identifier;
    final double lastDiscoveredTime;
    final String name;
    final double rssi;

    public DeviceInformation(String str, String str2, double d, double d2) {
        this.name = str;
        this.identifier = str2;
        this.rssi = d;
        this.lastDiscoveredTime = d2;
    }

    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public double getRssi() {
        return this.rssi;
    }

    public double getLastDiscoveredTime() {
        return this.lastDiscoveredTime;
    }

    public String toString() {
        return "DeviceInformation{name=" + this.name + ",identifier=" + this.identifier + ",rssi=" + this.rssi + ",lastDiscoveredTime=" + this.lastDiscoveredTime + "}";
    }
}
