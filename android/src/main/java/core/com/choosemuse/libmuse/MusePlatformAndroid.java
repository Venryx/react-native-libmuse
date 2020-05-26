package com.choosemuse.libmuse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

final class MusePlatformAndroid extends MusePlatformInterface {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    /* access modifiers changed from: private */
    public static final UUID M42_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int MSG_LOG = 2;
    private static final int MSG_SOCKET = 1;
    private static final int READ_SIZE = 256;
    private static final String TAG = "libmuse platform";
    private final AtomicBoolean alreadyReturnedConnectionHandle = new AtomicBoolean(false);
    private byte[] buffer;
    private List bufferedOutputs = new ArrayList();
    private Thread connectThread = null;
    /* access modifiers changed from: private */
    public final BluetoothAdapter mAdapter;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            boolean z = true;
            if (i == 1) {
                MusePlatformAndroid.this.handleSocketMsg(message);
            } else if (i == 2) {
                MusePlatformAndroid musePlatformAndroid = MusePlatformAndroid.this;
                String str = (String) message.obj;
                if (message.arg1 == 0) {
                    z = false;
                }
                musePlatformAndroid.addLog(str, z);
            }
        }
    };
    private BluetoothSocket mSocket = null;
    /* access modifiers changed from: private */
    public String macAddress;
    private Muse muse = null;
    /* access modifiers changed from: private */
    public boolean wantConnection = false;

    MusePlatformAndroid(String str) {
        this.macAddress = str;
        this.buffer = new byte[256];
        this.mAdapter = MuseManagerAndroid.getInstance().getDefaultAdapter();
    }

    /* access modifiers changed from: package-private */
    public void setMuse(Muse muse2) {
        this.muse = muse2;
    }

    private void addLog(String str) {
        addLog(str, false);
    }

    /* access modifiers changed from: private */
    public void addLog(String str, boolean z) {
        String str2 = this.macAddress;
        if (str2 == null) {
            str2 = "No Mac Android";
        }
        HwLogger.log(str2, "MusePlatformAndroid", str, "", z);
    }

    /* access modifiers changed from: private */
    public void handleSocketMsg(Message message) {
        synchronized (this) {
            try {
                if (message.obj != null) {
                    MuseLog.m14i("received new socket " + message.obj.toString());
                    addLog("handleSocketMsg - got new socket");
                    this.mSocket = (BluetoothSocket) message.obj;
                }
                if (!this.wantConnection || this.mSocket == null) {
                    eventuallyCloseSocket();
                } else {
                    flushBufferedWrites();
                }
                reallyJoin(this.connectThread);
                this.connectThread = null;
                if (this.wantConnection && this.mSocket == null) {
                    MuseLog.m14i("couldn't connect, requesting disconnect");
                    addLog("handleSocketMsg - can't connect, requesting disconnect", true);
                    if (this.muse != null) {
                        this.muse.disconnect();
                    } else {
                        MuseLog.m19w("Could not notify Muse of connection failure");
                        addLog("handleSocketMsg - can't notify muse of connection failure", true);
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this) {
            z = this.mSocket != null;
        }
        return z;
    }

    public void connect() {
        try {
            MuseLog.m17v("platform connect requested");
            synchronized (this) {
                this.wantConnection = true;
                this.bufferedOutputs.clear();
                if (this.connectThread == null) {
                    if (this.mSocket == null) {
                        MuseLog.m17v("new connection thread");
                        this.connectThread = new ConnectThread(this, (Object) null);
                        addLog("connect - starting connection thread");
                        this.connectThread.start();
                        return;
                    }
                }
                MuseLog.m17v("connect socket/thread already running");
                addLog("connect - socket/thread already running");
            }
        } catch (Throwable th) {
            MuseLog.m10e("Caught exception: " + th.toString());
            MuseLog.m10e(Arrays.toString(th.getStackTrace()));
            addLog("connect - Caught exception: " + th.toString(), true);
            throw th;
        }
    }

    public void disconnect() {
        MuseLog.m17v("platform disconnect requested");
        addLog("disconnect - called");
        synchronized (this) {
            try {
                this.wantConnection = false;
                if (this.connectThread != null) {
                    addLog("disconnect - do nothing, still want connection");
                    return;
                }
                if (this.mSocket != null) {
                    addLog("disconnect - eventuallyCloseSocket");
                    eventuallyCloseSocket();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void eventuallyCloseSocket() {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                synchronized (this) {
                    if (!MusePlatformAndroid.this.wantConnection) {
                        MusePlatformAndroid.this.closeSocket();
                    }
                }
            }
        }, 5000);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(5:1|2|(3:4|5|6)|7|8) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0012 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void closeSocket() {
        /*
            r2 = this;
            monitor-enter(r2)
            android.bluetooth.BluetoothSocket r0 = r2.mSocket     // Catch:{ all -> 0x0014 }
            if (r0 == 0) goto L_0x0012
            java.lang.String r0 = "closing socket"
            com.choosemuse.libmuse.MuseLog.m17v(r0)     // Catch:{ all -> 0x0014 }
            android.bluetooth.BluetoothSocket r0 = r2.mSocket     // Catch:{ all -> 0x0014 }
            r1 = 0
            r2.mSocket = r1     // Catch:{ all -> 0x0014 }
            r0.close()     // Catch:{ IOException -> 0x0012 }
        L_0x0012:
            monitor-exit(r2)     // Catch:{ all -> 0x0014 }
            return
        L_0x0014:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0014 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.choosemuse.libmuse.MusePlatformAndroid.closeSocket():void");
    }

    public String getAddress() {
        return this.macAddress;
    }

    public String getRemoteDeviceName() {
        try {
            String name = this.mAdapter.getRemoteDevice(this.macAddress).getName();
            if (name != null) {
                return name;
            }
            MuseLog.m19w("getRemoteDeviceName() received null; returning empty string");
            return "";
        } catch (Throwable th) {
            MuseLog.m10e("Caught exception: " + th.toString());
            MuseLog.m10e(Arrays.toString(th.getStackTrace()));
            throw th;
        }
    }

    public ConnectionHandle getHandle() {
        try {
            if (this.alreadyReturnedConnectionHandle.getAndSet(true)) {
                MuseLog.m12f("getHandle() multiple calls");
            }
            return new AndroidConnectionHandle(this);
        } catch (Throwable th) {
            MuseLog.m10e("Caught exception: " + th.toString());
            MuseLog.m10e(Arrays.toString(th.getStackTrace()));
            throw th;
        }
    }

    public boolean hasBytes() {
        boolean z;
        synchronized (this) {
            z = false;
            try {
                if (this.mSocket != null && this.mSocket.getInputStream().available() > 0) {
                    z = true;
                }
            } catch (IOException e) {
                try {
                    MuseLog.m19w("Muse hasBytes() failed");
                    e.printStackTrace();
                    closeSocket();
                    connect();
                } catch (Throwable th) {
                    MuseLog.m10e("Caught exception: " + th.toString());
                    MuseLog.m10e(Arrays.toString(th.getStackTrace()));
                    throw th;
                }
            }
        }
        return z;
    }

    public byte[] getBytes() {
    	// v-changed
		//ByteArrayOutputStream byteArrayOutputStream;
		ByteArrayOutputStream byteArrayOutputStream = null;

        byte[] byteArray;
        synchronized (this) {
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                if (this.mSocket != null) {
                    byteArrayOutputStream.write(this.buffer, 0, this.mSocket.getInputStream().read(this.buffer));
                }
            } catch (IOException e) {
                MuseLog.m19w("getBytes() from Muse failed");
                e.printStackTrace();
                byteArrayOutputStream.reset();
                closeSocket();
                connect();
            } catch (Throwable th) {
                MuseLog.m10e("Caught exception: " + th.toString());
                MuseLog.m10e(Arrays.toString(th.getStackTrace()));
                throw th;
            }
            byteArray = byteArrayOutputStream.toByteArray();
            try {
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return byteArray;
    }

    public boolean writeBinaryBytes(byte[] bArr) {
        boolean z;
        synchronized (this) {
            z = false;
            try {
                if (this.mSocket != null) {
                    this.mSocket.getOutputStream().write(bArr);
                } else {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
                    byteArrayOutputStream.write(bArr, 0, bArr.length);
                    this.bufferedOutputs.add(byteArrayOutputStream);
                }
                z = true;
            } catch (IOException e) {
                MuseLog.m19w("writeBytes() to Muse failed");
                e.printStackTrace();
                closeSocket();
                connect();
            } catch (Throwable th) {
                MuseLog.m10e("Caught exception: " + th.toString());
                MuseLog.m10e(Arrays.toString(th.getStackTrace()));
                throw th;
            }
        }
        return z;
    }

    private void flushBufferedWrites() {
        synchronized (this) {
            try {
                if (this.mSocket == null) {
                    this.bufferedOutputs.clear();
                    return;
                }

                // v-changed
				//for (ByteArrayOutputStream byteArrayOutputStream : this.bufferedOutputs) {
                for (Object entry : this.bufferedOutputs) {
					ByteArrayOutputStream byteArrayOutputStream = (ByteArrayOutputStream) entry;

					MuseLog.m17v("buffered write: " + byteArrayOutputStream.toString());
                    writeBinaryBytes(byteArrayOutputStream.toByteArray());
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException unused) {
                    }
                }
                this.bufferedOutputs.clear();
            } catch (Throwable th) {
                MuseLog.m10e("Caught exception: " + th.toString());
                MuseLog.m10e(Arrays.toString(th.getStackTrace()));
                throw th;
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:0|1|3|2) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:0:0x0000 */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:1:?, LOOP_START, MTH_ENTER_BLOCK, SYNTHETIC, Splitter:B:0:0x0000] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void reallyJoin(java.lang.Thread r1) {
        /*
            r0 = this;
        L_0x0000:
            r1.join()     // Catch:{ InterruptedException -> 0x0000 }
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.choosemuse.libmuse.MusePlatformAndroid.reallyJoin(java.lang.Thread):void");
    }

    private class ConnectThread extends Thread {
        private ConnectThread() {
        }

        public void sendSocket(BluetoothSocket bluetoothSocket) {
            Message obtainMessage = MusePlatformAndroid.this.mHandler.obtainMessage();
            obtainMessage.what = 1;
            obtainMessage.obj = bluetoothSocket;
            MuseLog.m17v("sending socket: " + obtainMessage.obj);
            obtainMessage.sendToTarget();
        }

        private void sendLog(String str, boolean z) {
            Message obtainMessage = MusePlatformAndroid.this.mHandler.obtainMessage();
            obtainMessage.what = 2;
            obtainMessage.obj = str;
            obtainMessage.arg1 = z ? 1 : 0;
            MuseLog.m17v("sending log: " + obtainMessage.obj);
            obtainMessage.sendToTarget();
        }

        public void run() {
            // v-changed
			//BluetoothSocket bluetoothSocket;
			BluetoothSocket bluetoothSocket = null;

            BluetoothDevice remoteDevice = MusePlatformAndroid.this.mAdapter.getRemoteDevice(MusePlatformAndroid.this.macAddress);
            MusePlatformAndroid.this.mAdapter.cancelDiscovery();
            BluetoothSocket bluetoothSocket2 = null;
            try {
                BluetoothSocket createRfcommSocketToServiceRecord = remoteDevice.createRfcommSocketToServiceRecord(MusePlatformAndroid.M42_UUID);
                try {
                    createRfcommSocketToServiceRecord.connect();
                    sendSocket(createRfcommSocketToServiceRecord);
                } catch (Exception e) {
                    if (Build.VERSION.SDK_INT < 19) {
                        try {
                            MuseLog.m19w("connect failed with " + e.getMessage() + ", falling back to reflection method and trying again");
                            sendLog("connection thread - failed: " + e.getMessage() + ", trying reflection method.", false);
                            Thread.sleep(500);
                            bluetoothSocket = (BluetoothSocket) remoteDevice.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE}).invoke(remoteDevice, new Object[]{1});
                            bluetoothSocket.connect();
                            bluetoothSocket2 = bluetoothSocket;
                        } catch (Exception unused) {
                            sendLog("connection thread - reflection method also failed", true);
                        } catch (Throwable th) {
                            th = th;
                            createRfcommSocketToServiceRecord = null;
                        }
                    } else {
                        MuseLog.m10e("connect() failed: " + e.getMessage());
                        sendLog("connection thread - connection failed: " + e.getMessage(), true);
                    }
                    sendSocket(bluetoothSocket2);
                } catch (Throwable th2) {
                    createRfcommSocketToServiceRecord = bluetoothSocket;

                    // v-changed
                    //th = th2;
					Throwable th = th2;

                    sendSocket(createRfcommSocketToServiceRecord);
                    throw th;
                }

            // v-changed
            //} catch (IOException e2) {
			} catch (Throwable e2) {

            	MuseLog.m10e("connect() failed. Could not create socket" + e2.getMessage());
                sendLog("connection thread - could not create socket", false);
                sendSocket((BluetoothSocket) null);
            }
        }

        ConnectThread(MusePlatformAndroid musePlatformAndroid, Object obj) {
            this();
        }
    }
}
