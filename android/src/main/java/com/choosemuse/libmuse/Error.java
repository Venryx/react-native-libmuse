package com.choosemuse.libmuse;

public final class Error {
    final int code;
    final String info;
    final ErrorType type;

    public Error(ErrorType errorType, int i, String str) {
        this.type = errorType;
        this.code = i;
        this.info = str;
    }

    public ErrorType getType() {
        return this.type;
    }

    public int getCode() {
        return this.code;
    }

    public String getInfo() {
        return this.info;
    }

    public String toString() {
        return "Error{type=" + this.type + ",code=" + this.code + ",info=" + this.info + "}";
    }
}
