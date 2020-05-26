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

class LibMuseModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        var onInit: Action? = null // hook for custom Android code

        lateinit var main: LibMuseModule
    }
    init {
        main = this
        if (onInit != null) onInit!!.Run()
    }
    // public version of this.reactApplicationContext
    var reactContext = reactContext

    override fun getName(): String {
        return "LibMuse"
    }

    fun SendEvent_JS(eventName: String?, vararg args: Any) {
        val argsList = Arguments.createArray()
        for (arg in args) {
            when (arg) {
                null -> argsList.pushNull()
                is Boolean -> argsList.pushBoolean((arg as Boolean?)!!)
                is Int -> argsList.pushInt((arg as Int?)!!)
                is Double -> argsList.pushDouble((arg as Double?)!!)
                is String -> argsList.pushString(arg as String?)
                //is WritableArray -> argsList.pushArray(arg as WritableArray?)
                //is WritableMap -> argsList.pushMap(arg as WritableMap?)
                is List<*> -> argsList.pushArray(RNHelpers.ToWritableArray(arg))
                is HashMap<*, *> -> argsList.pushMap(RNHelpers.ToWritableMap(arg))
                else -> {
                    throw RuntimeException("Event args must be one of: Boolean, Integer, Double, String, WritableArray, WritableMap")
                }
            }
        }

        if (vMuse.module != null) {
            val jsModuleEventEmitter = vMuse.module.reactContext.getJSModule(RCTDeviceEventEmitter::class.java)
            jsModuleEventEmitter.emit(eventName, argsList)
        }
    }

    val vMuse = VMuse()
    var packetSetSize = 10
    var currentMuseDataPacketSet = Arguments.createArray()
    init {
        //vMuse.module = this
        vMuse.AddEventListener("*") { realEventInfo ->
            val eventName = realEventInfo[0] as String
            val eventArgs = realEventInfo[1] as List<Any>
            SendEvent_JS(eventName, eventArgs)
        }
        vMuse.AddEventListener("OnChangeMuseList") { args ->
            val muses = args[0] as List<Muse>
            val museList = Arguments.createArray()
            for (muse in muses) {
                val museInfo = Arguments.createMap()
                museInfo.putString("name", muse.name)
                museInfo.putString("macAddress", muse.macAddress)
                museInfo.putDouble("lastDiscoveredTime", muse.lastDiscoveredTime)
                museInfo.putDouble("rssi", muse.rssi)
                museList.pushMap(museInfo)
            }
            SendEvent_JS("OnChangeMuseList", museList)
        }
        vMuse.AddEventListener("") { args ->
            val packet = args[0] as VMuseDataPacket
            val packetForRN = packet.ToMap()

            // if you want to send a received packet right away, every frame, use this
            // ==========

            //SendEvent_JS("OnReceiveMuseDataPacket", packetForRN);

            // otherwise, use the default below, of buffering then sending in a set
            // ==========

            // add to packet-set
            currentMuseDataPacketSet.pushMap(packetForRN)

            // send packet-set to js, if ready
            if (currentMuseDataPacketSet.size() == packetSetSize) {
                SendEvent_JS("OnReceiveMuseDataPacketSet", currentMuseDataPacketSet)
                currentMuseDataPacketSet = Arguments.createArray() // create new set
            }
        }
    }

    @ReactMethod fun Init() { vMuse.Init() }
    @ReactMethod fun StopSearch() { vMuse.StopSearch() }
    @ReactMethod fun StartSearch() { vMuse.StartSearch() }
    @ReactMethod fun RestartSearch() { vMuse.RestartSearch() }
    @ReactMethod fun Connect(museIndex: Int) { vMuse.Connect(museIndex) }
    @ReactMethod fun Disconnect() { vMuse.Disconnect() }
    @ReactMethod fun TogglePaused() { vMuse.TogglePaused() }
}