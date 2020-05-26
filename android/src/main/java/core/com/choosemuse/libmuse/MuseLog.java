package com.choosemuse.libmuse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MuseLog {
    /* renamed from: r */
    public static void m16r(String str) {
        log(Severity.SEV_VERBOSE, str, true);
    }

    /* renamed from: v */
    public static void m17v(String str) {
        log(Severity.SEV_VERBOSE, str);
    }

    /* renamed from: i */
    public static void m14i(String str) {
        log(Severity.SEV_INFO, str);
    }

    /* renamed from: w */
    public static void m19w(String str) {
        log(Severity.SEV_WARN, str);
    }

    /* renamed from: e */
    public static void m10e(String str) {
        log(Severity.SEV_ERROR, str);
    }

    /* renamed from: f */
    public static void m12f(String str) {
        log(Severity.SEV_FATAL, str);
    }

    /* renamed from: d */
    public static void m8d(String str) {
        log(Severity.SEV_DEBUG, str);
    }

    /* renamed from: v */
    public static void m18v(String str, Throwable th) {
        log(Severity.SEV_VERBOSE, str, th);
    }

    /* renamed from: i */
    public static void m15i(String str, Throwable th) {
        log(Severity.SEV_INFO, str, th);
    }

    /* renamed from: w */
    public static void m20w(String str, Throwable th) {
        log(Severity.SEV_WARN, str, th);
    }

    /* renamed from: e */
    public static void m11e(String str, Throwable th) {
        log(Severity.SEV_ERROR, str, th);
    }

    /* renamed from: f */
    public static void m13f(String str, Throwable th) {
        log(Severity.SEV_FATAL, str, th);
    }

    /* renamed from: d */
    public static void m9d(String str, Throwable th) {
        log(Severity.SEV_DEBUG, str, th);
    }

    public static void log(Severity severity, String str) {
        log(severity, str, false);
    }

    public static void log(Severity severity, String str, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        log(severity, str + "\n" + stringWriter, false);
    }

    public static void log(Severity severity, String str, boolean z) {
        LogManager.instance().writeLog(severity, z, "PLATFORM", str);
    }
}
