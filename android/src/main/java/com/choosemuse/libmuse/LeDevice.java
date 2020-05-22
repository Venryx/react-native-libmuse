package com.choosemuse.libmuse;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"NewApi"})
class LeDevice extends MuseLeDevice {
    private static final int CONNECTOR_CONNECT_REQUEST = 0;
    private static final int CONNECTOR_DISCONNECT_REQUEST = 1;
    private static final int CONNECTOR_REQUEST_START_RECEIVING = 3;
    private static final int CONNECTOR_REQUEST_STOP_RECEIVING = 4;
    private static final int CONNECTOR_WRITE_CHARACTERISTIC = 2;
    private static final int GATT_CHARACTERISTIC_CHANGED = 100;
    private static final int GATT_CONNECTED = 101;
    private static final int GATT_DESCRIPTOR_WRITE = 103;
    private static final int GATT_DISCONNECTED = 102;
    private static final int GATT_SERVICES_DISCOVERED = 104;
    private static final int HANDLER_REFRESH_DEVICE_CACHE = 1000;
    private static final int REFRESH_DEVICE_CACHE_RETRY_DELAY = 10;
    private static final EnumMap enumMap = new EnumMap(CharacteristicId.class);
    private int androidDeviceOSLevel;
    private final BluetoothDevice bluetoothDevice;
    /* access modifiers changed from: private */
    public final Map characteristicToId = new HashMap();
    private MuseLeDeviceDelegate delegate;
    private BluetoothGatt gatt;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            try {
                int i = message.what;
                if (i == 0) {
                    LeDevice.this.handleConnectorConnectRequest();
                } else if (i == 1) {
                    LeDevice.this.handleConnectorDisconnectRequest();
                } else if (i == 2) {
                    LeDevice.this.handleConnectorWriteCharacteristic(message.arg1, (byte[]) message.obj);
                } else if (i == 3) {
                    LeDevice.this.handleConnectorWriteDescriptor(message.arg1, true);
                } else if (i == 4) {
                    LeDevice.this.handleConnectorWriteDescriptor(message.arg1, false);
                } else if (i != 1000) {
                    switch (i) {
                        case 100:
                            LeDevice.this.handleGattCharacteristicChanged(message.arg1, (byte[]) message.obj);
                            return;
                        case 101:
                            LeDevice.this.handleGattConnected(message.arg1);
                            return;
                        case 102:
                            LeDevice.this.handleGattDisconnected(message.arg1);
                            return;
                        case 103:
                            LeDevice.this.handleGattDescriptorWrite(message.arg1, (byte[]) message.obj);
                            return;
                        case LeDevice.GATT_SERVICES_DISCOVERED /*104*/:
                            LeDevice.this.handleGattServicesDiscovered(message.arg1);
                            return;
                        default:
                            MuseLog.m19w("Unrecognized message type: " + message.what);
                            return;
                    }
                } else {
                    boolean unused = LeDevice.this.refreshDeviceCache();
                }
            } catch (RuntimeException e) {
                MuseLog.m11e("*** Exception thrown ***", e);
                throw e;
            }
        }
    };
    private final Map idToCharacteristic = new HashMap();
    public String leVersion = MuseManagerAndroid.MUSE_VERSION_UNKNOWN;
    private final Context mContext;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            LeDevice leDevice = LeDevice.this;
            leDevice.sendMessage(100, leDevice.idAsInt((CharacteristicId) leDevice.characteristicToId.get(bluetoothGattCharacteristic)), bluetoothGattCharacteristic.getValue());
        }

        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (i != 0) {
                MuseLog.m14i("onCharacteristicRead status: " + i);
            }
        }

        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            if (i != 0) {
                MuseLog.m14i("onCharacteristicWrite status:" + i);
            }
        }

        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int i, int i2) {
            if (i != GattErrorCode.GATT_SUCCESS.status()) {
                MuseLog.m10e("Error during connection state change:  status=" + GattErrorCode.getErrorString(i) + "(" + i + "), newState=" + i2);
            }
            if (i2 == 2) {
                LeDevice.this.sendMessage(101, i);
            } else if (i2 == 0) {
                LeDevice.this.sendMessage(102, i);
            }
        }

        public void onDescriptorRead(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            MuseLog.m14i("onDescriptorRead status:" + i);
        }

        public void onDescriptorWrite(BluetoothGatt bluetoothGatt, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            LeDevice.this.sendMessage(103, i, bluetoothGattDescriptor.getValue());
        }

        public void onServicesDiscovered(BluetoothGatt bluetoothGatt, int i) {
            LeDevice.this.sendMessage((int) LeDevice.GATT_SERVICES_DISCOVERED, i);
        }
    };
    private long mLastDiscoveredMicros = -1;
    private int mNumCharacteristicsRegistered = 0;

    public boolean isPaired() {
        return false;
    }

    static {
        CharacteristicId[] values = CharacteristicId.values();
        int length = values.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            enumMap.put(values[i], Integer.valueOf(i2));
            i++;
            i2++;
        }
    }

    public LeDevice(Context context, BluetoothDevice bluetoothDevice2, long j) {
        this.mContext = context;
        this.bluetoothDevice = bluetoothDevice2;
        this.mLastDiscoveredMicros = j * 1000;
        this.androidDeviceOSLevel = Build.VERSION.SDK_INT;
    }

    public void rediscoveredAt(long j) {
        synchronized (this) {
            this.mLastDiscoveredMicros = j * 1000;
        }
    }

    public DeviceInformation getInfo() {
        DeviceInformation deviceInformation;
        synchronized (this) {
            String name = this.bluetoothDevice.getName();
            if (name == null) {
                MuseLog.m19w("null name from the OS");
                name = "";
            }
            String str = name;
            String address = this.bluetoothDevice.getAddress();
            if (address == null) {
                MuseLog.m19w("null MAC from the OS");
                address = "";
            }
            deviceInformation = new DeviceInformation(str, address, 0.0d, (double) this.mLastDiscoveredMicros);
        }
        return deviceInformation;
    }

    public int getOsLevel() {
        return this.androidDeviceOSLevel;
    }

    public void setDelegate(MuseLeDeviceDelegate museLeDeviceDelegate) {
        synchronized (this) {
            this.delegate = museLeDeviceDelegate;
        }
    }

    public void requestConnect() {
        sendMessage(0);
    }

    public void requestDisconnect() {
        sendMessage(1);
    }

    public void requestStartReceiving(CharacteristicId characteristicId) {
        MuseLog.m14i("request start receiving: " + characteristicId + " int=" + idAsInt(characteristicId));
        sendMessage(3, idAsInt(characteristicId));
    }

    public void requestStopReceiving(CharacteristicId characteristicId) {
        MuseLog.m14i("request stop receiving: " + characteristicId);
        sendMessage(4, idAsInt(characteristicId));
    }

    public void writeValueToCharacteristic(CharacteristicId characteristicId, byte[] bArr) {
        sendMessage(2, idAsInt(characteristicId), bArr);
    }

    /* access modifiers changed from: private */
    public int idAsInt(CharacteristicId characteristicId) {
        return ((Integer) enumMap.get(characteristicId)).intValue();
    }

    private void sendMessage(int i) {
        sendMessage(i, 0, (Object) null);
    }

    /* access modifiers changed from: private */
    public void sendMessage(int i, int i2) {
        sendMessage(i, i2, (Object) null);
    }

    private void sendMessage(int i, Object obj) {
        sendMessage(i, 0, obj);
    }

    /* access modifiers changed from: private */
    public void sendMessage(int i, int i2, Object obj) {
        Message obtainMessage = this.handler.obtainMessage();
        obtainMessage.what = i;
        obtainMessage.arg1 = i2;
        obtainMessage.obj = obj;
        obtainMessage.sendToTarget();
    }

    /* access modifiers changed from: private */
    public void handleConnectorConnectRequest() {
        MuseLog.m14i("connect request");
        this.mNumCharacteristicsRegistered = 0;
        this.idToCharacteristic.clear();
        this.characteristicToId.clear();
        this.gatt = this.bluetoothDevice.connectGatt(this.mContext, false, this.mGattCallback);
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            if (this.androidDeviceOSLevel >= 21) {
                bluetoothGatt.requestConnectionPriority(1);
            }
            refreshDeviceCache();
            return;
        }
        MuseLog.m19w("Unable to connect.  Bluetooth may be off.");
        sendMessage(102);
    }

    /* access modifiers changed from: private */
    public void handleConnectorWriteCharacteristic(int i, byte[] bArr) {
        BluetoothGattCharacteristic bluetoothGattCharacteristic = (BluetoothGattCharacteristic) this.idToCharacteristic.get(CharacteristicId.values()[i]);
        bluetoothGattCharacteristic.setWriteType(1);
        bluetoothGattCharacteristic.setValue(bArr);
        this.gatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    /* access modifiers changed from: private */
    public void handleConnectorWriteDescriptor(int i, boolean z) {
        boolean z2;
        MuseLog.m14i("write descriptor request " + i);
        if (this.gatt == null) {
            disconnectWithError("handleConnectorWriteDescriptor", "Gatt instance not created.");
            return;
        }
        CharacteristicId characteristicId = CharacteristicId.values()[i];
        MuseLog.m14i("id is = " + characteristicId + " idToCharacteristic size = " + this.idToCharacteristic.size());
        BluetoothGattCharacteristic bluetoothGattCharacteristic = (BluetoothGattCharacteristic) this.idToCharacteristic.get(characteristicId);
        if (bluetoothGattCharacteristic != null) {
            List<BluetoothGattDescriptor> descriptors = bluetoothGattCharacteristic.getDescriptors();
            if (descriptors.size() > 0) {
                BluetoothGattDescriptor bluetoothGattDescriptor = descriptors.get(0);
                if (z) {
                    z2 = this.gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                    bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    z2 = this.gatt.setCharacteristicNotification(bluetoothGattCharacteristic, false);
                    bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }
                if (!z2) {
                    disconnectWithError("handleConnectorWriteDescriptor", "gatt.setCharacteristicNotification failed.");
                } else {
                    this.gatt.writeDescriptor(bluetoothGattDescriptor);
                }
            } else {
                addLog("handleConnectorWriteDescriptor descriptor size is 0", 1);
            }
        } else {
            disconnectWithError("handleConnectorWriteDescriptor", "Could not locate characteristic: " + characteristicId + ".  Are you in bootloader mode?");
        }
    }

    /* access modifiers changed from: private */
    public void handleConnectorDisconnectRequest() {
        MuseLog.m14i("disconnect request");
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    private String getStateString(String str, int i) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("GattStatusCode", GattErrorCode.getErrorString(i));
            return jSONObject.toString();
        } catch (JSONException unused) {
            MuseLog.m10e("Couldn't create JSON object in getStateString");
            return "MuseLeDeviceAndroid " + str + " can't create JSON";
        }
    }

    private void addLog(String str, int i) {
        addLog(str, i, i != 0);
    }

    private void addLog(String str, int i, boolean z) {
        String stateString = getStateString(str, i);
        String address = this.bluetoothDevice.getAddress();
        if (address == null) {
            address = "No Mac LeDevice";
        }
        HwLogger.log(address, "MuseLeDeviceAndroid", str, stateString, z);
    }

    private void disconnectWithError(String str, String str2) {
        disconnectWithError(str, str2, 0);
    }

    private void disconnectWithError(String str, String str2, int i) {
        String str3 = "Requesting disconnect: " + str2;
        MuseLog.m10e(str3);
        addLog(str + ": " + str3, i, true);
        requestDisconnect();
    }

    /* access modifiers changed from: private */
    public void handleGattCharacteristicChanged(int i, byte[] bArr) {
        CharacteristicId characteristicId = CharacteristicId.values()[i];
        synchronized (this) {
            this.delegate.didReceiveValueForCharacteristic(characteristicId, bArr);
        }
    }

    /* access modifiers changed from: private */
    public void handleGattConnected(int i) {
        MuseLog.m14i("handleGattConnected, gattStatus=" + i);
        addLog("handleGattConnected", i);
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        } else {
            disconnectWithError("handleGattConnected", "Gatt connected called but gatt instance is null");
        }
    }

    /* access modifiers changed from: private */
    public void handleGattDisconnected(int i) {
        BluetoothGatt bluetoothGatt = this.gatt;
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            this.gatt = null;
        }
        addLog("handleGattDisconnected", i);
        synchronized (this) {
            this.delegate.didDisconnect();
        }
    }

    /* access modifiers changed from: private */
    public void handleGattDescriptorWrite(int i, byte[] bArr) {
        MuseLog.m14i("handleGattDescriptorWrite status:" + i + " numCharacteristics=" + this.mNumCharacteristicsRegistered);
        byte b = bArr[0];
        if (b == 1 || b == 2) {
            addLog("handleGattDescriptorWrite::startReceiving", i);
            synchronized (this) {
                this.delegate.didStartReceiving();
            }
        } else if (b == 0) {
            addLog("handleGattDescriptorWrite::stopReceiving", i);
            synchronized (this) {
                this.delegate.didStopReceiving();
            }
        } else {
            MuseLog.m10e("Descriptor written contained unexpected starting value: " + b);
        }
    }

    /* access modifiers changed from: private */
    public void handleGattServicesDiscovered(int i) {
        addLog("handleGattServicesDiscovered", i);
        MuseLog.m14i("handleGattServicesDiscovered status:" + i);
        if (i != 0) {
            disconnectWithError("handleGattServicesDiscovered", "Error during service discovery:  status = " + GattErrorCode.getErrorString(i) + "(" + i + ")", i);
            return;
        }
        BluetoothGattService service = this.gatt.getService(MuseManagerAndroid.museUuid());
        if (service == null) {
            disconnectWithError("handleGattServicesDiscovered", "Could not iterate services. gattService is null");
            return;
        }
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        MuseLog.m14i("found " + characteristics.size() + " characteristics");
        boolean z = false;
        for (BluetoothGattCharacteristic next : characteristics) {
            CharacteristicId characteristicId = null;
            try {
                characteristicId = CharacteristicMapper.instance().characteristicForUuid(UuidToByteArray(UuidReverse(next.getUuid())).array());
            } catch (Exception unused) {
                z = true;
            }
            if (characteristicId != null) {
                MuseLog.m14i("UUID: " + characteristicId.toString() + " - " + next.getUuid());
                this.idToCharacteristic.put(characteristicId, next);
                this.characteristicToId.put(next, characteristicId);
            } else {
                String uuid = next.getUuid().toString();
                char c = 65535;
                switch (uuid.hashCode()) {
                    case -1710359933:
                        if (uuid.equals("273e0011-4c4d-454d-96be-f03bac821358")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -1426329578:
                        if (uuid.equals("273e000c-4c4d-454d-96be-f03bac821358")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -733227881:
                        if (uuid.equals("273e000d-4c4d-454d-96be-f03bac821358")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -40126184:
                        if (uuid.equals("273e000e-4c4d-454d-96be-f03bac821358")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 652975513:
                        if (uuid.equals("273e000f-4c4d-454d-96be-f03bac821358")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1891505666:
                        if (uuid.equals("273e0010-4c4d-454d-96be-f03bac821358")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    characteristicId = CharacteristicId.PPG_A;
                } else if (c == 1) {
                    characteristicId = CharacteristicId.PPG_B;
                } else if (c == 2) {
                    characteristicId = CharacteristicId.PPG_C;
                } else if (c == 3) {
                    characteristicId = CharacteristicId.PPG_X;
                } else if (c == 4) {
                    characteristicId = CharacteristicId.PPG_Y;
                } else if (c == 5) {
                    characteristicId = CharacteristicId.PPG_Z;
                }
                if (characteristicId != null) {
                    MuseLog.m14i("MU03-UUID: " + characteristicId.toString() + " - " + next.getUuid());
                } else {
                    MuseLog.m14i("Unknown UUID: " + next.getUuid());
                }
            }
        }
        if (z) {
            MuseLog.m14i("Muse Version - MU03");
            this.leVersion = MuseManagerAndroid.MUSE_VERSION_MU03;
        } else {
            MuseLog.m14i("Muse Version - MU02");
            this.leVersion = MuseManagerAndroid.MUSE_VERSION_MU02;
        }
        synchronized (this) {
            this.delegate.didConnect();
        }
        int size = this.idToCharacteristic.size();
        if (size != 11 && size != 1) {
            disconnectWithError("handleGattServicesDiscovered", "unexpected number of characteristics discovered: expected " + 11 + " or " + 1 + " , encountered " + size);
        }
    }

    private UUID UuidReverse(UUID uuid) {
        ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
        wrap.putLong(Long.reverseBytes(uuid.getLeastSignificantBits()));
        wrap.putLong(Long.reverseBytes(uuid.getMostSignificantBits()));
        wrap.rewind();
        return new UUID(wrap.getLong(), wrap.getLong());
    }

    private ByteBuffer UuidToByteArray(UUID uuid) {
        ByteBuffer wrap = ByteBuffer.wrap(new byte[16]);
        wrap.putLong(uuid.getMostSignificantBits());
        wrap.putLong(uuid.getLeastSignificantBits());
        return wrap;
    }

    /* access modifiers changed from: private */
    public boolean refreshDeviceCache() {
        try {
            MuseLog.m14i("entering refreshDeviceCache()");
            Method method = this.gatt.getClass().getMethod("refresh", new Class[0]);
            if (method != null) {
                boolean booleanValue = ((Boolean) method.invoke(this.gatt, new Object[0])).booleanValue();
                MuseLog.m14i("BluetoothGatt.refresh() returned " + booleanValue);
                if (!booleanValue) {
                    Message obtainMessage = this.handler.obtainMessage();
                    obtainMessage.what = 1000;
                    this.handler.sendMessageDelayed(obtainMessage, 10);
                }
                return booleanValue;
            }
        } catch (Exception e) {
            MuseLog.m14i("exception " + e.toString() + " occurred in refreshDeviceCache()");
        }
        return false;
    }

    private enum GattErrorCode {
        GATT_SUCCESS(0),
        GATT_INVALID_HANDLE(1),
        GATT_READ_NOT_PERMIT(2),
        GATT_WRITE_NOT_PERMIT(3),
        GATT_INVALID_PDU(4),
        GATT_INSUF_AUTHENTICATION(5),
        GATT_REQ_NOT_SUPPORTED(6),
        GATT_INVALID_OFFSET(7),
        GATT_INSUF_AUTHORIZATION(8),
        GATT_PREPARE_Q_FULL(9),
        GATT_NOT_FOUND(10),
        GATT_NOT_LONG(11),
        GATT_INSUF_KEY_SIZE(12),
        GATT_INVALID_ATTR_LEN(13),
        GATT_ERR_UNLIKELY(14),
        GATT_INSUF_ENCRYPTION(15),
        GATT_UNSUPPORT_GRP_TYPE(16),
        GATT_INSUF_RESOURCE(17),
        GATT_ILLEGAL_PARAMETER(135),
        GATT_NO_RESOURCES(128),
        GATT_INTERNAL_ERROR(129),
        GATT_WRONG_STATE(130),
        GATT_DB_FULL(131),
        GATT_BUSY(132),
        GATT_ERROR(133),
        GATT_CMD_STARTED(134),
        GATT_PENDING(136),
        GATT_AUTH_FAIL(137),
        GATT_MORE(138),
        GATT_INVALID_CFG(139),
        GATT_SERVICE_STARTED(140),
        GATT_ENCRYPED_NO_MITM(141),
        GATT_NOT_ENCRYPTED(142);
        
        private int status;

        private GattErrorCode(int i) {
            this.status = i;
        }

        public int status() {
            return this.status;
        }

        public static String getErrorString(int i) {
            String str = null;
            for (int i2 = 0; i2 < values().length; i2++) {
                if (values()[i2].status() == i) {
                    str = values()[i2].toString();
                }
            }
            return str;
        }
    }
}
