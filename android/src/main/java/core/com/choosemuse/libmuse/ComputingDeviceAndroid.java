package com.choosemuse.libmuse;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class ComputingDeviceAndroid extends ComputingDeviceInterface {
    private static ComputingDeviceAndroid instance;
    private String apiKey = "InteraXon";
    private String appName = "";
    private String appVersion = "";
    private String bluetoothVersion = "";
    private volatile Context context;
    private PackageInfo packageInfo;

    ComputingDeviceAndroid() {
    }

    private String capitalize(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        char charAt = str.charAt(0);
        if (Character.isUpperCase(charAt)) {
            return str;
        }
        return Character.toUpperCase(charAt) + str.substring(1);
    }

    private String getHardwareModelName() {
        String str = Build.MANUFACTURER;
        String str2 = Build.MODEL;
        if (str2.startsWith(str)) {
            return capitalize(str2);
        }
        return capitalize(str) + " " + str2;
    }

    private String getSupportedAbis() {
        return Build.VERSION.SDK_INT >= 21 ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI;
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x003c A[SYNTHETIC, Splitter:B:22:0x003c] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x006c A[SYNTHETIC, Splitter:B:34:0x006c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getProcessorSpeed() {
        /*
            r6 = this;
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"
            r0.<init>(r1)
            r1 = 0
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0036 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException -> 0x0036 }
            r3.<init>(r0)     // Catch:{ IOException -> 0x0036 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0036 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0031, all -> 0x002e }
            r0.<init>()     // Catch:{ IOException -> 0x0031, all -> 0x002e }
        L_0x0017:
            java.lang.String r1 = r2.readLine()     // Catch:{ IOException -> 0x0031, all -> 0x002e }
            if (r1 == 0) goto L_0x0021
            r0.append(r1)     // Catch:{ IOException -> 0x0031, all -> 0x002e }
            goto L_0x0017
        L_0x0021:
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x0031, all -> 0x002e }
            r2.close()     // Catch:{ IOException -> 0x0029 }
            goto L_0x0046
        L_0x0029:
            r1 = move-exception
            r1.printStackTrace()
            goto L_0x0046
        L_0x002e:
            r0 = move-exception
            r1 = r2
            goto L_0x006a
        L_0x0031:
            r0 = move-exception
            r1 = r2
            goto L_0x0037
        L_0x0034:
            r0 = move-exception
            goto L_0x006a
        L_0x0036:
            r0 = move-exception
        L_0x0037:
            r0.printStackTrace()     // Catch:{ all -> 0x0034 }
            if (r1 == 0) goto L_0x0044
            r1.close()     // Catch:{ IOException -> 0x0040 }
            goto L_0x0044
        L_0x0040:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0044:
            java.lang.String r0 = ""
        L_0x0046:
            long r1 = java.lang.Long.parseLong(r0)     // Catch:{ NumberFormatException -> 0x0069 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x0069 }
            r3.<init>()     // Catch:{ NumberFormatException -> 0x0069 }
            double r1 = (double) r1
            r4 = 4696837146684686336(0x412e848000000000, double:1000000.0)
            java.lang.Double.isNaN(r1)
            double r1 = r1 / r4
            java.lang.String r1 = java.lang.Double.toString(r1)     // Catch:{ NumberFormatException -> 0x0069 }
            r3.append(r1)     // Catch:{ NumberFormatException -> 0x0069 }
            java.lang.String r1 = "MHz"
            r3.append(r1)     // Catch:{ NumberFormatException -> 0x0069 }
            java.lang.String r0 = r3.toString()     // Catch:{ NumberFormatException -> 0x0069 }
        L_0x0069:
            return r0
        L_0x006a:
            if (r1 == 0) goto L_0x0074
            r1.close()     // Catch:{ IOException -> 0x0070 }
            goto L_0x0074
        L_0x0070:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0074:
            goto L_0x0076
        L_0x0075:
            throw r0
        L_0x0076:
            goto L_0x0075
        */
        throw new UnsupportedOperationException("Method not decompiled: com.choosemuse.libmuse.ComputingDeviceAndroid.getProcessorSpeed():java.lang.String");
    }

    private int getProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003d, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x004f A[SYNTHETIC, Splitter:B:23:0x004f] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0066 A[SYNTHETIC, Splitter:B:28:0x0066] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getMemorySizeBytes() {
        /*
            r7 = this;
            java.io.File r0 = new java.io.File
            java.lang.String r1 = "/proc/meminfo"
            r0.<init>(r1)
            r1 = 0
            r2 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ IOException -> 0x0049 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ IOException -> 0x0049 }
            r5.<init>(r0)     // Catch:{ IOException -> 0x0049 }
            r4.<init>(r5)     // Catch:{ IOException -> 0x0049 }
        L_0x0014:
            java.lang.String r0 = r4.readLine()     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            if (r0 == 0) goto L_0x0038
            java.util.Locale r1 = java.util.Locale.US     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            java.lang.String r1 = r0.toLowerCase(r1)     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            java.lang.String r5 = "memtotal"
            boolean r1 = r1.contains(r5)     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            if (r1 == 0) goto L_0x0014
            java.lang.String r1 = "[^0-9]+"
            java.lang.String r5 = ""
            java.lang.String r0 = r0.replaceAll(r1, r5)     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            long r0 = java.lang.Long.parseLong(r0)     // Catch:{ IOException -> 0x0043, all -> 0x0041 }
            r5 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 / r5
            r2 = r0
        L_0x0038:
            r4.close()     // Catch:{ IOException -> 0x003c }
            goto L_0x0052
        L_0x003c:
            r0 = move-exception
            r0.printStackTrace()
            goto L_0x0052
        L_0x0041:
            r0 = move-exception
            goto L_0x0064
        L_0x0043:
            r0 = move-exception
            r1 = r4
            goto L_0x004a
        L_0x0046:
            r0 = move-exception
            r4 = r1
            goto L_0x0064
        L_0x0049:
            r0 = move-exception
        L_0x004a:
            r0.printStackTrace()     // Catch:{ all -> 0x0046 }
            if (r1 == 0) goto L_0x0052
            r1.close()     // Catch:{ IOException -> 0x003c }
        L_0x0052:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r2)
            java.lang.String r1 = "MB"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0064:
            if (r4 == 0) goto L_0x006e
            r4.close()     // Catch:{ IOException -> 0x006a }
            goto L_0x006e
        L_0x006a:
            r1 = move-exception
            r1.printStackTrace()
        L_0x006e:
            goto L_0x0070
        L_0x006f:
            throw r0
        L_0x0070:
            goto L_0x006f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.choosemuse.libmuse.ComputingDeviceAndroid.getMemorySizeBytes():java.lang.String");
    }

    private String getTimezone() {
        return TimeZone.getDefault().getDisplayName(false, 0, Locale.US);
    }

    private int getTimezoneOffsetSeconds() {
        return TimeZone.getDefault().getOffset(new Date().getTime()) / 1000;
    }

    public static ComputingDeviceAndroid getInstance() {
        if (instance == null) {
            synchronized (ComputingDeviceAndroid.class) {
                if (instance == null) {
                    instance = new ComputingDeviceAndroid();
                }
            }
        }
        return instance;
    }

    public void setContext(Context context2) {
        this.context = context2;
        if (context2 != null) {
            try {
                this.packageInfo = context2.getPackageManager().getPackageInfo(context2.getPackageName(), 0);
                if (this.packageInfo != null) {
                    this.appName = this.packageInfo.packageName;
                    this.appVersion = this.packageInfo.versionName;
                }
            } catch (PackageManager.NameNotFoundException unused) {
                MuseLog.m10e("Could not get package info...exception thrown");
            }
        }
        ComputingDeviceBridge.getInstance().setComputingDeviceInterface(this);
    }

    public String getRecorderName() {
        return this.appName;
    }

    public String getRecorderVersion() {
        return this.appVersion;
    }

    public String getRecorderKey() {
        return this.apiKey;
    }

    public ComputingDeviceConfiguration getComputingDeviceConfig() {
        return new ComputingDeviceConfiguration("Android", Build.VERSION.RELEASE, getHardwareModelName(), Build.MODEL, getSupportedAbis(), getProcessorSpeed(), getProcessorCount(), getMemorySizeBytes(), this.bluetoothVersion, getTimezone(), getTimezoneOffsetSeconds());
    }
}
