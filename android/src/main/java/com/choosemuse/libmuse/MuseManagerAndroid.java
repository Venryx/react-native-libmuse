package com.choosemuse.libmuse;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public final class MuseManagerAndroid extends MuseManager {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    public static String MUSE_VERSION_MU01 = "MU01";
    public static String MUSE_VERSION_MU02 = "MU02";
    public static String MUSE_VERSION_MU03 = "MU03";
    public static String MUSE_VERSION_UNKNOWN = "UNKNOWN";
    private static MuseManagerAndroid instance;
    /* access modifiers changed from: private */
    public final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private volatile Context context;
    /* access modifiers changed from: private */
    public long expirationTime;
    /* access modifiers changed from: private */
    public final Handler handler = new Handler(Looper.getMainLooper());
	private final EventLoop asyncLoop = new HandlerEventLoop(this.handler); // v-moved (to after handler init)
    private final boolean isMuseEnabled = RestrictedFeatures.getInstance().isEnabled(RestrictedFeatures.ENABLE_MUSE);
    /* access modifiers changed from: private */
    public final AtomicBoolean isScanning = new AtomicBoolean(false);
    private final boolean isSmithxEnabled = RestrictedFeatures.getInstance().isEnabled(RestrictedFeatures.ENABLE_SMITHX);
    private final Map leDevices = new HashMap();
    /* access modifiers changed from: private */
    public final Map museList = new HashMap();
    private volatile MuseListener museListener;
    private final BroadcastReceiver pairingMonitor = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MuseManagerAndroid.this.handlePairing(intent);
        }
    };
    private Runnable refreshPairedMu01s = new Runnable() {
        public void run() {
            MuseLog.m17v("Refreshing paired MU-01s");
            for (BluetoothDevice next : MuseManagerAndroid.this.adapter.getBondedDevices()) {
                String name = next.getName();
                if (name == null) {
                    MuseLog.m14i("Ignoring bonded device with null name. MAC: " + next.getAddress());
                } else if (name.toLowerCase(Locale.US).startsWith("muse") && MuseManagerAndroid.this.scanExtra.isMu01(next)) {
                    MuseManagerAndroid.this.discoverMu01(next);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final ScanExtra scanExtra = createScanExtra();
    private HashMap statsMap = new HashMap();
    private Runnable unregistrationRunnable = new Runnable() {
        public void run() {
            boolean z;
            if (MuseManagerAndroid.this.isScanning.get()) {
                long access$500 = MuseManagerAndroid.this.expirationTime;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                ArrayList arrayList = new ArrayList();
                Iterator it = MuseManagerAndroid.this.getMuses().iterator();
                while (it.hasNext()) {
                    Muse muse = (Muse) it.next();
                    ConnectionState connectionState = muse.getConnectionState();
                    if (muse.isLowEnergy() && (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.UNKNOWN)) {
                        long access$5002 = MuseManagerAndroid.this.expirationTime - (elapsedRealtime - (((long) muse.getLastDiscoveredTime()) / 1000));
                        if (access$5002 < 0) {
                            arrayList.add(muse);
                        } else if (access$5002 < access$500) {
                            access$500 = access$5002;
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    Iterator it2 = arrayList.iterator();
                    synchronized (MuseManagerAndroid.this.museList) {
                        z = false;
                        while (it2.hasNext()) {
                            z |= MuseManagerAndroid.this.museList.remove(((Muse) it2.next()).getMacAddress()) != null;
                        }
                    }
                    if (z) {
                        MuseManagerAndroid.this.museListChanged();
                    }
                }
                MuseManagerAndroid.this.handler.postDelayed(this, access$500);
            }
        }
    };

    private interface ScanExtra {
        boolean isMu01(BluetoothDevice bluetoothDevice);

        void startScanningMu02();

        void stopScanningMu02();
    }

    private MuseManagerAndroid() {
        LogManager.instance().setLogListener(new AndroidLogListener());
        removeFromListAfter(30);
    }

    public static synchronized MuseManagerAndroid getInstance() {
        synchronized (MuseManagerAndroid.class) {
            if (instance != null) {
                MuseManagerAndroid museManagerAndroid = instance;
                return museManagerAndroid;
            }
            instance = new MuseManagerAndroid();
            MuseManagerAndroid museManagerAndroid2 = instance;
            return museManagerAndroid2;
        }
    }

    private ScanExtra createScanExtra() {
        try {
            MuseLog.m17v("trying to init for API 21");
            return new Api21Scan(this, (Object) null);
        } catch (NoClassDefFoundError e) {
            MuseLog.m17v(e.toString());
            try {
                MuseLog.m17v("trying to init for API 19");
                return new Api19Scan(this, (Object) null);
            } catch (NoClassDefFoundError e2) {
                MuseLog.m17v(e2.toString());
                MuseLog.m17v("trying to init for API 15");
                return new Api15Scan(this, (Object) null);
            }
        }
    }

    public static UUID museUuid() {
        return UUID.fromString("0000FE8D-0000-1000-8000-00805F9B34FB");
    }

    private ArrayList createMuseList() {
        ArrayList arrayList;
        synchronized (this.museList) {
            arrayList = new ArrayList();

            // v-changed
			//for (Map.Entry entry : this.museList.entrySet()) {
            for (Object entry_raw : this.museList.entrySet()) {
				Map.Entry entry = (Map.Entry) entry_raw;

                boolean equals = ((Muse) entry.getValue()).getName().substring(0, 4).equals("Muse");
                if ((equals && this.isMuseEnabled) || (!equals && this.isSmithxEnabled)) {
                    arrayList.add(entry.getValue());
                }
            }
        }
        return arrayList;
    }

    public BluetoothAdapter getDefaultAdapter() {
        return this.adapter;
    }

    public ArrayList getMuses() {
        return createMuseList();
    }

    /* access modifiers changed from: private */
    public void museListChanged() {
        if (this.museListener != null) {
            this.museListener.museListChanged();
        }
    }

    public void setContext(Context context2) {
        this.context = context2;
        ComputingDeviceAndroid.getInstance().setContext(context2);
    }

    public void setMuseListener(MuseListener museListener2) {
        this.museListener = museListener2;
    }

    private void startScanningMu01() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        try {
            this.context.registerReceiver(this.pairingMonitor, intentFilter);
        } catch (NullPointerException unused) {
            MuseLog.m10e("You must call setContext before startListening");
        }
        this.handler.post(this.refreshPairedMu01s);
    }

    private void stopScanningMu01() {
        this.handler.removeCallbacks(this.refreshPairedMu01s);
        try {
            this.context.unregisterReceiver(this.pairingMonitor);
        } catch (IllegalArgumentException unused) {
            MuseLog.m12f("unregisterReceiver failed. Call stopListening from the same thread as startListening.");
        }
    }

    public void startListening() {
        if (!this.isScanning.getAndSet(true)) {
            MuseLog.m17v("start listening");
            this.handler.removeCallbacks(this.unregistrationRunnable);
            synchronized (this.museList) {
                this.museList.clear();
            }
            startScanningMu01();
            try {
                this.scanExtra.startScanningMu02();
            } catch (Exception e) {
                MuseLog.m10e("startListening failed. Maybe Bluetooth is off? Exception: " + e + "\n" + Arrays.toString(e.getStackTrace()));
                stopListening();
            }
            postUnregistrationRunnable();
        }
    }

    public void stopListening() {
        if (this.isScanning.getAndSet(false)) {
            MuseLog.m17v("stop listening");
            this.scanExtra.stopScanningMu02();
            stopScanningMu01();
            this.handler.removeCallbacks(this.unregistrationRunnable);
        }
    }

    public void removeFromListAfter(long j) {
        this.expirationTime = j * 1000;
        if (this.isScanning.get()) {
            this.handler.removeCallbacks(this.unregistrationRunnable);
            postUnregistrationRunnable();
        }
    }

    private void postUnregistrationRunnable() {
        long j = this.expirationTime;
        if (j > 0) {
            this.handler.postDelayed(this.unregistrationRunnable, j);
        }
    }

    /* access modifiers changed from: private */
    public void discoverMu01(BluetoothDevice bluetoothDevice) {
        String address = bluetoothDevice.getAddress();
        checkMainThread();
        if (((Muse) this.museList.get(address)) == null) {
            MusePlatformAndroid musePlatformAndroid = new MusePlatformAndroid(address);
            Muse muse = MuseFactory.getMuse(musePlatformAndroid, this.asyncLoop);
            if (this.isMuseEnabled) {
                musePlatformAndroid.setMuse(muse);
                synchronized (this.museList) {
                    this.museList.put(address, muse);
                }
                museListChanged();
            }
        }
    }

    public AdvertisingStats getAdvertisingStats(Muse muse) {
        DiscoveryStats discoveryStats;
        synchronized (this.statsMap) {
            discoveryStats = (DiscoveryStats) this.statsMap.get(muse.getName());
        }
        AdvertisingStats advertisingStats = null;
        if (discoveryStats != null) {
            advertisingStats = new AdvertisingStats(discoveryStats.nTimesSeen, discoveryStats.avgInterval, discoveryStats.stdDev, discoveryStats.maxInterval, discoveryStats.lastInterval > 40.0d, discoveryStats.hasBadMac);
        }
        return advertisingStats == null ? new AdvertisingStats(0, 0.0d, 0.0d, 0.0d, false, false) : advertisingStats;
    }

    public void resetAdvertisingStats() {
        synchronized (this.statsMap) {
        	// v-changed
			//for (DiscoveryStats resetStats : this.statsMap.values()) {
			for (Object entry : this.statsMap.values()) {
				DiscoveryStats resetStats = (DiscoveryStats) entry;

                resetStats.resetStats();
            }
        }
    }

    /* access modifiers changed from: private */
    public void discoverMu02(BluetoothDevice bluetoothDevice) {
        checkMainThread();
        Muse muse = (Muse) this.museList.get(bluetoothDevice.getAddress());
        if (muse == null) {
            LeDevice leDevice = new LeDevice(this.context, bluetoothDevice, SystemClock.elapsedRealtime());
            Muse muse2 = MuseLeFactory.getMuse(leDevice, this.asyncLoop);
            boolean equals = muse2.getName().substring(0, 4).equals("Muse");
            if ((equals && this.isMuseEnabled) || (!equals && this.isSmithxEnabled)) {
                synchronized (this.museList) {
                    this.museList.put(bluetoothDevice.getAddress(), muse2);
                }
                this.leDevices.put(bluetoothDevice.getAddress(), leDevice);
                museListChanged();
            }
            muse = muse2;
        } else if (!muse.isLowEnergy()) {
            MuseLog.m10e("MU-01 rediscovered as MU-02?! address: " + bluetoothDevice.getAddress());
        } else {
            ((LeDevice) this.leDevices.get(bluetoothDevice.getAddress())).rediscoveredAt(SystemClock.elapsedRealtime());
        }
        synchronized (this.statsMap) {
            if (muse != null) {
                if (muse.isLowEnergy()) {
                    DiscoveryStats discoveryStats = (DiscoveryStats) this.statsMap.get(muse.getName());
                    if (discoveryStats == null) {
                        DiscoveryStats discoveryStats2 = new DiscoveryStats();
                        discoveryStats2.sawIt(muse);
                        this.statsMap.put(muse.getName(), discoveryStats2);
                    } else {
                        discoveryStats.sawIt(muse);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlePairing(Intent intent) {
        BluetoothDevice bluetoothDevice;
        checkMainThread();
        String action = intent.getAction();
        if (((action.hashCode() == 2116862345 && action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED")) ? (char) 0 : 65535) != 0 || (bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE")) == null || bluetoothDevice.getName() == null || !bluetoothDevice.getName().toLowerCase(Locale.US).startsWith("muse-")) {
            return;
        }
        if (bluetoothDevice.getBondState() == 12) {
            discoverMu01(bluetoothDevice);
        } else if (bluetoothDevice.getBondState() == 10 && this.museList.get(bluetoothDevice.getAddress()) != null) {
            MuseLog.m17v("Clearing device " + bluetoothDevice.getAddress());
            synchronized (this.museList) {
                this.museList.remove(bluetoothDevice.getAddress());
            }
            museListChanged();
        }
    }

    private void checkMainThread() {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            MuseLog.m12f("Current thread is not main thread.\n" + Arrays.toString(Thread.currentThread().getStackTrace()));
        }
    }

    static {
        try {
            System.loadLibrary("muse_android");
			Log.v("MUSE", "Successfully loaded libmuse_android.so."); // v
        } catch (UnsatisfiedLinkError unused) {
            Log.v("MUSE", "Failed to load libmuse_android.so. Make sure the jni symbols are accessible somehow.");
        }
    }

    @TargetApi(15)
    private class Api15Scan implements ScanExtra {
        public boolean isMu01(BluetoothDevice bluetoothDevice) {
            return true;
        }

        public void startScanningMu02() {
        }

        public void stopScanningMu02() {
        }

        private Api15Scan() {
        }

        Api15Scan(MuseManagerAndroid museManagerAndroid, Object obj) {
            this();
        }
    }

    @TargetApi(19)
    private class Api19Scan implements ScanExtra {
        private final BluetoothAdapter.LeScanCallback scanCallback;

        private Api19Scan() {
			// v-changed
			//this.scanCallback = new BluetoothAdapter.LeScanCallback(MuseManagerAndroid.this) {
			this.scanCallback = new BluetoothAdapter.LeScanCallback() {

                static final /* synthetic */ boolean $assertionsDisabled = false;

                public void onLeScan(final BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
                    String name = bluetoothDevice.getName();
                    if (name != null && name.contains("Muse")) {
                        if (bluetoothDevice.getType() != 2) {
                            MuseLog.m10e("MU-02 strange type: " + bluetoothDevice.getType() + ", address: " + bluetoothDevice.getAddress());
                            return;
                        }
                        MuseManagerAndroid.this.handler.post(new Runnable() {
                            public void run() {
                                MuseManagerAndroid.this.discoverMu02(bluetoothDevice);
                            }
                        });
                    }
                }
            };
        }

        public boolean isMu01(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getType() == 1;
        }

        public void startScanningMu02() {
            if (MuseManagerAndroid.this.adapter.isEnabled()) {
                MuseManagerAndroid.this.adapter.startLeScan(this.scanCallback);
                return;
            }
            throw new RuntimeException("startLeScan with Bluetooth off");
        }

        public void stopScanningMu02() {
            MuseManagerAndroid.this.adapter.stopLeScan(this.scanCallback);
        }

        Api19Scan(MuseManagerAndroid museManagerAndroid, Object obj) {
            this();
        }
    }

    @TargetApi(21)
    private class Api21Scan implements ScanExtra {
        private final List filters;
        private final ScanCallback scanCallback;
        private BluetoothLeScanner scanner;
        private final ScanSettings settings;

        private Api21Scan() {
            // v-changed
        	//this.scanCallback = new ScanCallback(MuseManagerAndroid.this) {
			this.scanCallback = new ScanCallback() {

                static final /* synthetic */ boolean $assertionsDisabled = false;

                // v-removed
                /*static {
                    Class<MuseManagerAndroid> cls = MuseManagerAndroid.class;
                }*/

                public void onScanResult(int i, ScanResult scanResult) {
                    BluetoothDevice device = scanResult.getDevice();
                    if (device.getType() != 2) {
                        MuseLog.m10e("MU-02 strange type: " + device.getType() + ", address: " + device.getAddress());
                        return;
                    }
                    MuseManagerAndroid.this.discoverMu02(device);
                }
            };
            this.filters = Collections.singletonList(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(MuseManagerAndroid.museUuid())).build());
            this.settings = new ScanSettings.Builder().setScanMode(2).build();
        }

        public boolean isMu01(BluetoothDevice bluetoothDevice) {
            return bluetoothDevice.getType() == 1;
        }

        public void startScanningMu02() {
            this.scanner = MuseManagerAndroid.this.adapter.getBluetoothLeScanner();
            this.scanner.startScan(this.filters, this.settings, this.scanCallback);
        }

        public void stopScanningMu02() {
            if (this.scanner == null) {
                MuseLog.m19w("stopScanningMu02 noop: null scanner");
                return;
            }
            int state = MuseManagerAndroid.this.adapter.getState();
            if (state != 12) {
                MuseLog.m19w("stopScanningMu02 noop: bluetooth not on, state " + state);
                return;
            }
            this.scanner.stopScan(this.scanCallback);
        }

        Api21Scan(MuseManagerAndroid museManagerAndroid, Object obj) {
            this();
        }
    }

    public String getMuseVersion(Muse muse) {
        if (!muse.isLowEnergy()) {
            return MUSE_VERSION_MU01;
        }
        String macAddress = muse.getMacAddress();
        if (this.leDevices.containsKey(macAddress)) {
            return ((LeDevice) this.leDevices.get(macAddress)).leVersion;
        }
        return MUSE_VERSION_UNKNOWN;
    }

    class DiscoveryStats {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        double avgInterval;
        boolean firstTime = true;
        boolean hasBadMac;
        double lastInterval;
        double lastSeenTime;
        String mac;
        double maxInterval;
        int nTimesSeen = 0;
        String name;
        double stdDev;
        double sumIntervals;
        double sumSqIntervals;

        // v-removed
        /*static {
            Class<MuseManagerAndroid> cls = MuseManagerAndroid.class;
        }*/

        DiscoveryStats() {
        }

        public void resetStats() {
            this.firstTime = true;
            this.avgInterval = 0.0d;
            this.nTimesSeen = 0;
            this.lastInterval = 0.0d;
            this.maxInterval = 0.0d;
            this.sumIntervals = 0.0d;
            this.sumSqIntervals = 0.0d;
            this.stdDev = 0.0d;
            this.hasBadMac = false;
        }

        public void sawIt(Muse muse) {
            double elapsedRealtime = (double) SystemClock.elapsedRealtime();
            Double.isNaN(elapsedRealtime);
            double d = elapsedRealtime / 1000.0d;
            double d2 = 0.0d;
            if (this.firstTime) {
                this.firstTime = false;
                this.avgInterval = 0.0d;
                this.nTimesSeen = 1;
                this.lastInterval = 0.0d;
                this.maxInterval = 0.0d;
                this.sumIntervals = 0.0d;
                this.sumSqIntervals = 0.0d;
                this.stdDev = 0.0d;
                this.name = muse.getName();
                this.mac = muse.getMacAddress();
                this.hasBadMac = false;
            } else {
                this.nTimesSeen++;
                double d3 = (double) (this.nTimesSeen - 1);
                if (!this.mac.equals(muse.getMacAddress())) {
                    this.hasBadMac = true;
                }
                this.lastInterval = d - this.lastSeenTime;
                double d4 = this.lastInterval;
                if (d4 > this.maxInterval) {
                    this.maxInterval = d4;
                }
                double d5 = this.avgInterval;
                Double.isNaN(d3);
                double d6 = d3 - 1.0d;
                double d7 = this.lastInterval;
                Double.isNaN(d3);
                this.avgInterval = ((d5 * d6) + d7) / d3;
                this.sumIntervals += d7;
                this.sumSqIntervals += d7 * d7;
                if (d3 > 1.0d) {
                    double d8 = this.sumSqIntervals;
                    double d9 = this.sumIntervals;
                    Double.isNaN(d3);
                    d2 = (d8 - ((d9 * d9) / d3)) / d6;
                }
                this.stdDev = Math.sqrt(d2);
            }
            this.lastSeenTime = d;
        }

        public void print() {
            double elapsedRealtime = (double) SystemClock.elapsedRealtime();
            Double.isNaN(elapsedRealtime);
            String format = String.format("%.2f", new Object[]{Double.valueOf((elapsedRealtime / 1000.0d) - this.lastSeenTime)});
            String format2 = String.format("%.2f", new Object[]{Double.valueOf(this.avgInterval)});
            String format3 = String.format("%.2f", new Object[]{Double.valueOf(this.maxInterval)});
            String format4 = String.format("%.2f", new Object[]{Double.valueOf(this.stdDev)});
            String.format("%.2f", new Object[]{Double.valueOf(this.lastInterval)});
            MuseLog.m14i(this.name + "  " + this.mac + "  LS: " + format + ",  n: " + this.nTimesSeen + ",  max: " + format3 + ",  avg: " + format2 + ", StdDev: " + format4);
            if (this.hasBadMac) {
                MuseLog.m10e("*** corrupted mac ***");
            }
        }
    }
}
