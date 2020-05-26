package com.choosemuse.libmuse;

import java.util.ArrayList;

public final class DspData {
    final ArrayList floatArray;
    final ArrayList intArray;
    final String type;
    final String version;

    public DspData(String str, ArrayList arrayList, ArrayList arrayList2, String str2) {
        this.type = str;
        this.floatArray = arrayList;
        this.intArray = arrayList2;
        this.version = str2;
    }

    public String getType() {
        return this.type;
    }

    public ArrayList getFloatArray() {
        return this.floatArray;
    }

    public ArrayList getIntArray() {
        return this.intArray;
    }

    public String getVersion() {
        return this.version;
    }

    public String toString() {
        return "DspData{type=" + this.type + ",floatArray=" + this.floatArray + ",intArray=" + this.intArray + ",version=" + this.version + "}";
    }
}
