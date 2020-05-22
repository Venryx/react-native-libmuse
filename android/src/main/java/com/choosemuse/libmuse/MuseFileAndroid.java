package com.choosemuse.libmuse;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class MuseFileAndroid extends MuseFile {
    private static final String TAG = "libmuse file";
    private File file;
    private InputStream inStream;
    private OutputStream outStream;
    private boolean reading = false;
    private boolean writing = false;

    MuseFileAndroid(File file2) {
        this.file = file2;
    }

    public boolean open(boolean z) {
        if (z) {
            try {
                if (this.writing) {
                    return true;
                }
                this.outStream = new BufferedOutputStream(new FileOutputStream(this.file, true));
                this.writing = true;
                return true;
            } catch (FileNotFoundException e) {
                Log.e(TAG, "open() failed", e);
                return false;
            }
        } else if (this.reading) {
            return true;
        } else {
        	// v-changed
			//this.inStream = new BufferedInputStream(new FileInputStream(this.file));
			try {
				this.inStream = new BufferedInputStream(new FileInputStream(this.file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			this.reading = true;
            return true;
        }
    }

    public boolean write(byte[] bArr) {
        if (!this.writing) {
            Log.e(TAG, "file was not opened for writing");
            return false;
        }
        try {
            this.outStream.write(bArr);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "write() failed", e);
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0013, code lost:
        r1 = new byte[r6];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public byte[] read(int r6) {
        /*
            r5 = this;
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r0.<init>()
            boolean r1 = r5.reading
            java.lang.String r2 = "libmuse file"
            if (r1 == 0) goto L_0x0029
            java.io.InputStream r1 = r5.inStream     // Catch:{ IOException -> 0x0022 }
            int r1 = r1.available()     // Catch:{ IOException -> 0x0022 }
            if (r6 > r1) goto L_0x002e
            byte[] r1 = new byte[r6]     // Catch:{ IOException -> 0x0022 }
            java.io.InputStream r3 = r5.inStream     // Catch:{ IOException -> 0x0022 }
            r4 = 0
            int r3 = r3.read(r1, r4, r6)     // Catch:{ IOException -> 0x0022 }
            if (r3 != r6) goto L_0x002e
            r0.write(r1, r4, r3)     // Catch:{ IOException -> 0x0022 }
            goto L_0x002e
        L_0x0022:
            r6 = move-exception
            java.lang.String r1 = "read() failed"
            android.util.Log.e(r2, r1, r6)
            goto L_0x002e
        L_0x0029:
            java.lang.String r6 = "file was not opened for reading"
            android.util.Log.e(r2, r6)
        L_0x002e:
            byte[] r6 = r0.toByteArray()
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.choosemuse.libmuse.MuseFileAndroid.read(int):byte[]");
    }

    public boolean close(boolean z) {
        if (z) {
            try {
                if (this.writing) {
                    this.outStream.close();
                    this.writing = false;
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "close() failed", e);
                return false;
            }
        }
        if (!z && this.reading) {
        	// v-changed
			//this.inStream.close();
			try {
				this.inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.reading = false;
        }
        return true;
    }
}
