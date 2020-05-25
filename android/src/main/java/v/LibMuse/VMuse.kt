package v.LibMuse

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.choosemuse.libmuse.*
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter

interface Action {
    fun Run()
}

interface Func<T> {
    fun Run(data: T)
}

class VMuse {
    companion object {
        const val TAG = "VMuse"
        var mainActivity: Activity? = null
    }

    lateinit var module: LibMuseModule

    fun SendEvent(eventName: String?, vararg args: Any) {
        val argsList = Arguments.createArray()
        for (arg in args) {
            when (arg) {
                null -> argsList.pushNull()
                is Boolean -> argsList.pushBoolean((arg as Boolean?)!!)
                is Int -> argsList.pushInt((arg as Int?)!!)
                is Double -> argsList.pushDouble((arg as Double?)!!)
                is String -> argsList.pushString(arg as String?)
                is WritableArray -> argsList.pushArray(arg as WritableArray?)
                else -> {
                    //Assert(arg instanceof WritableMap, "Event args must be one of: WritableArray, Boolean")
                    if (arg !is WritableMap) throw RuntimeException("Event args must be one of: Boolean, Integer, Double, String, WritableArray, WritableMap")
                    argsList.pushMap(arg as WritableMap?)
                }
            }
        }

        //SendEvent_Android(eventName, args.toList())
        SendEvent_Android(eventName, args)
        SendEvent_JS(eventName, argsList)

    }
    fun SendEvent_Android(eventName: String?, vararg args: Any) {
        if (eventListeners.containsKey(eventName)) {
            eventListeners[eventName]!!.forEach {
                //it(argsList)
                it(args.toList())
            }
        }
    }
    fun SendEvent_JS(eventName: String?, argsList: WritableArray) {
        if (this.module != null) {
            val jsModuleEventEmitter = this.module.reactContext.getJSModule(RCTDeviceEventEmitter::class.java)
            jsModuleEventEmitter.emit(eventName, argsList)
        }
    }

    // for Android API
    //var eventListeners = mutableMapOf<String, ArrayList<((args: WritableArray)->Any)>>()
    var eventListeners = mutableMapOf<String, ArrayList<((args: List<Any>)->Any)>>()
    fun AddEventListener(eventName: String, listener: ((args: List<Any>)->Any)) {
        val listenersForEventType = eventListeners.getOrPut(eventName, { arrayListOf() })
        listenersForEventType.add(listener)
    }

    // the MuseManager is how you detect Muse headbands and receive notifications when the list of available headbands changes
    var manager: MuseManagerAndroid? = null

    fun Init() {
        if (mainActivity == null) throw RuntimeException("VMuse.mainActivity not set. (set it in your main-activity's constructor)")

        // We need to set the context on MuseManagerAndroid before we can do anything.
        // This must come before other LibMuse API calls as it also loads the library.
        try {
            manager = MuseManagerAndroid.getInstance()
            manager!!.setContext(mainActivity)
        } catch (ex: Throwable) {
            throw RuntimeException("Failed to start muse-manager: $ex")
        }
        Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().string)

        // Muse 2016 (MU-02) headbands use Bluetooth Low Energy technology to simplify the connection process.
        // This requires the COARSE_LOCATION or FINE_LOCATION permissions. Make sure we have these permissions before proceeding.
        EnsurePermissions()
        AddMuseListListener()
    }

    /**
     * The ACCESS_COARSE_LOCATION permission is required to use the
     * Bluetooth Low Energy library and must be requested at runtime for Android 6.0+
     * On an Android 6.0 device, the following code will display 2 dialogs,
     * one to provide context and the second to request the permission.
     * On an Android device running an earlier version, nothing is displayed
     * as the permission is granted from the manifest.
     *
     *
     * If the permission is not granted, then Muse 2016 (MU-02) headbands will
     * not be discovered and a SecurityException will be thrown.
     */
    private fun EnsurePermissions() {
        if (ContextCompat.checkSelfPermission(mainActivity!!, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) return

        // We don't have the ACCESS_COARSE_LOCATION permission, so create the dialogs asking the user to grant it.

        // This is the context dialog which explains to the user the reason we are requesting
        // this permission.  When the user presses the positive (I Understand) button, the
        // standard Android permission dialog will be displayed (as defined in the button listener above).
        val introDialog = AlertDialog.Builder(mainActivity)
            .setTitle("Requesting permissions")
            .setMessage("Location-services permission needed for Bluetooth connection to work.")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(mainActivity!!, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            }
            .create()
        introDialog.show()
    }

    // make-so: root application calls this on-pause and on-exit
    fun StopSearch() {
        manager!!.stopListening()
    }

    fun StartSearch() {
        manager!!.startListening()
    }

    fun RestartSearch() {
        StopSearch() // clear the list of headbands and start fresh
        StartSearch()
    }

    fun AddMuseListListener() {
        // Register a listener to receive notifications of what Muse headbands we can connect to.
        manager!!.setMuseListener(object : MuseListener() {
            override fun museListChanged() {
                val museList = Arguments.createArray()
                for (muse in manager!!.muses) {
                    val museInfo = Arguments.createMap()
                    museInfo.putString("name", muse.name)
                    museInfo.putString("macAddress", muse.macAddress)
                    museInfo.putDouble("lastDiscoveredTime", muse.lastDiscoveredTime)
                    museInfo.putDouble("rssi", muse.rssi)
                    museList.pushMap(museInfo)
                }
                SendEvent("OnChangeMuseList", museList)
            }
        })
    }

    var muse: Muse? = null

    fun Connect(museIndex: Int = 0) {
        // Listening is expensive, so now that we know which headband the user wants to connect to, we can stop listening for other headbands.
        StopSearch()
        // Cache the Muse that the user has selected.
        muse = manager!!.muses[museIndex]

        /*if (muse == null) {
			Log.i(TAG, "Tried to connect to Muse at index " + museIndex + ", but it was lost from list before connection occurred.");
			return;
		}*/

        // Unregister all prior listeners and register our data listener to receive the MuseDataPacketTypes we are interested in.
        // If you do not register a listener for a particular data type, you will not receive data packets of that type.
        muse!!.unregisterAllListeners()
        AddConnectionListener()
        AddDataListener()

        // Initiate a connection to the headband and stream the data asynchronously.
        //muse.runAsynchronously();
        if (muse!!.isLowEnergy) {
            muse!!.setPreset(MusePreset.PRESET_20)
            //this.sample_freq = this.res.getInteger(C0441R.integer.sample_freq_mu_02);
            //isLowEnergy = true;
        } else {
            muse!!.setPreset(MusePreset.PRESET_12)
            //this.sample_freq = this.res.getInteger(C0441R.integer.sample_freq_mu_01);
            //this.pref_showAux = false;
            //isLowEnergy = false;
        }

        // todo: figure out which of these is correct, for matching with BlueMuse recordings
        // (I *think* it doesn't matter, ie. the notch-frequency only modulates the raw-eeg-derived frequency "packets")
        //this.muse.setNotchFrequency(NotchFrequency.NOTCH_NONE);
        //this.muse.setNotchFrequency(NotchFrequency.NOTCH_50HZ);
        //this.muse.setNotchFrequency(NotchFrequency.NOTCH_60HZ);
        muse!!.enableDataTransmission(true)
        //try {
        muse!!.runAsynchronously()
        //} catch (Exception e) {
        //	Log.e(TAG, "Got error:" + e);
        //}
    }

    fun Disconnect() {
        if (muse == null) return
        muse!!.disconnect()
    }

    fun AddConnectionListener() {
        muse!!.registerConnectionListener(object : MuseConnectionListener() {
            override fun receiveMuseConnectionPacket(packet: MuseConnectionPacket, muse: Muse) {
                val current = packet.currentConnectionState

                // Format a message to show the change of connection state in the UI.
                val status = packet.previousConnectionState.toString() + " -> " + current
                Log.i(TAG, status)
                if (current == ConnectionState.CONNECTED) {
                    Log.i(TAG, "Muse connected: " + muse.name)
                }
                if (current == ConnectionState.DISCONNECTED) {
                    Log.i(TAG, "Muse disconnected: " + muse.name)
                    // We have disconnected from the headband, so set our cached copy to null.
                    this@VMuse.muse = null
                }
                muse.setNumConnectTries(1000)
                SendEvent("OnChangeMuseConnectStatus", current.name.toLowerCase())
            }
        })
    }

    var customHandler: VMuseDataPacket.Listener? = null
    fun AddDataListener() {
        RegisterDataListener(object : MuseDataListener() {
            override fun receiveMuseDataPacket(basePacket: MuseDataPacket, muse: Muse) {
                val packetType = basePacket.packetType()
                // currently we just ignore other packet types
                if (packetType != MuseDataPacketType.EEG && packetType != MuseDataPacketType.ACCELEROMETER) return

                val packet = VMuseDataPacket(basePacket)
                val handled = customHandler!!.OnReceivePacket(packet)
                if (handled) return

                // load/prepare data
                when (packet.type) {
                    "eeg" -> packet.LoadEEGValues()
                    "accel" -> packet.LoadAccelValues()
                }
                val packetForRN = packet.ToMap()

                // if you want to send a received packet right away, every frame, use this
                // ==========

                //SendEvent_Android("OnReceiveMuseDataPacket", packetForRN);
                SendEvent_Android("OnReceiveMuseDataPacket_Android", packet);

                // otherwise, use the default below, of buffering then sending in a set
                // ==========

                // add to packet-set
                currentMuseDataPacketSet.pushMap(packetForRN)

                // send packet-set to js, if ready
                if (currentMuseDataPacketSet.size() == packetSetSize) {
                    SendEvent("OnReceiveMuseDataPacketSet", currentMuseDataPacketSet)
                    currentMuseDataPacketSet = Arguments.createArray() // create new set
                }
            }

            override fun receiveMuseArtifactPacket(p: MuseArtifactPacket, muse: Muse) {}
        })
    }

    var packetSetSize = 10
    var currentMuseDataPacketSet = Arguments.createArray()
    fun RegisterDataListener(listener: MuseDataListener?) {
        muse!!.registerDataListener(listener, MuseDataPacketType.EEG)
        muse!!.registerDataListener(listener, MuseDataPacketType.ALPHA_RELATIVE)
        muse!!.registerDataListener(listener, MuseDataPacketType.ACCELEROMETER)
        muse!!.registerDataListener(listener, MuseDataPacketType.BATTERY)
        muse!!.registerDataListener(listener, MuseDataPacketType.DRL_REF)
        muse!!.registerDataListener(listener, MuseDataPacketType.QUANTIZATION)
    }

    // whether the data-transmission from the headband is enabled
    private var transmissionEnabled = true

    fun TogglePaused() {
        if (muse == null) return
        transmissionEnabled = !transmissionEnabled
        muse!!.enableDataTransmission(transmissionEnabled)
    }
}