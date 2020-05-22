package com.choosemuse.libmuse;

public final class AnnotationData {
    final String data;
    final String eventId;
    final String eventType;
    final AnnotationFormat format;
    final String parentId;

    public AnnotationData(String str, AnnotationFormat annotationFormat, String str2, String str3, String str4) {
        this.data = str;
        this.format = annotationFormat;
        this.eventType = str2;
        this.eventId = str3;
        this.parentId = str4;
    }

    public String getData() {
        return this.data;
    }

    public AnnotationFormat getFormat() {
        return this.format;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventId() {
        return this.eventId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public String toString() {
        return "AnnotationData{data=" + this.data + ",format=" + this.format + ",eventType=" + this.eventType + ",eventId=" + this.eventId + ",parentId=" + this.parentId + "}";
    }
}
