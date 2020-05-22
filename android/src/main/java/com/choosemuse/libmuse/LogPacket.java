package com.choosemuse.libmuse;

public final class LogPacket {
    final String message;
    final boolean raw;
    final Severity severity;
    final String tag;
    final double timestamp;

    public LogPacket(Severity severity2, boolean z, String str, double d, String str2) {
        this.severity = severity2;
        this.raw = z;
        this.tag = str;
        this.timestamp = d;
        this.message = str2;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public boolean getRaw() {
        return this.raw;
    }

    public String getTag() {
        return this.tag;
    }

    public double getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "LogPacket{severity=" + this.severity + ",raw=" + this.raw + ",tag=" + this.tag + ",timestamp=" + this.timestamp + ",message=" + this.message + "}";
    }
}
