package com.choosemuse.libmuse;

public final class Result {
    final int code;
    final String info;
    final ResultLevel level;
    final String type;

    public Result(ResultLevel resultLevel, String str, int i, String str2) {
        this.level = resultLevel;
        this.type = str;
        this.code = i;
        this.info = str2;
    }

    public ResultLevel getLevel() {
        return this.level;
    }

    public String getType() {
        return this.type;
    }

    public int getCode() {
        return this.code;
    }

    public String getInfo() {
        return this.info;
    }

    public String toString() {
        return "Result{level=" + this.level + ",type=" + this.type + ",code=" + this.code + ",info=" + this.info + "}";
    }
}
