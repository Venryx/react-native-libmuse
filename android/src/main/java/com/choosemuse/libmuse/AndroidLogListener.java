package com.choosemuse.libmuse;

import android.util.Log;

public class AndroidLogListener extends LogListener {
    private Object LOCK = new Object();
    private String TAG = "MUSE";
    private StringBuilder raw = new StringBuilder();

    public void receiveLog(LogPacket logPacket) {
        synchronized (this.LOCK) {
            if (!logPacket.raw) {
                flushRaw();
                String str = logPacket.tag + " " + logPacket.message;
                switch (logPacket.severity) {
                    case SEV_INFO:
                        Log.i(this.TAG, str);
                        break;
                    case SEV_DEBUG:
                        Log.d(this.TAG, str);
                        break;
                    case SEV_VERBOSE:
                        Log.v(this.TAG, str);
                        break;
                    case SEV_WARN:
                        Log.w(this.TAG, str);
                        break;
                    case SEV_ERROR:
                        Log.e(this.TAG, str);
                        break;
                    case SEV_FATAL:
                        Log.e(this.TAG, str);
                        break;
                }
            } else {
                this.raw.append(logPacket.message);
                if (this.raw.length() > 1024) {
                    flushRaw();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void flushRaw() {
        if (this.raw.length() != 0) {
            String str = this.TAG;
            Log.v(str, "(raw) " + this.raw.toString());
            this.raw.setLength(0);
        }
    }
}
