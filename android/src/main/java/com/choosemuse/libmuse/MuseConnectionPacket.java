package com.choosemuse.libmuse;

public final class MuseConnectionPacket {
    final ConnectionState currentConnectionState;
    final ConnectionState previousConnectionState;

    public MuseConnectionPacket(ConnectionState connectionState, ConnectionState connectionState2) {
        this.previousConnectionState = connectionState;
        this.currentConnectionState = connectionState2;
    }

    public ConnectionState getPreviousConnectionState() {
        return this.previousConnectionState;
    }

    public ConnectionState getCurrentConnectionState() {
        return this.currentConnectionState;
    }

    public String toString() {
        return "MuseConnectionPacket{previousConnectionState=" + this.previousConnectionState + ",currentConnectionState=" + this.currentConnectionState + "}";
    }
}
