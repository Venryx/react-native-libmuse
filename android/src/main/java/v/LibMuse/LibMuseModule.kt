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

    val vMuse = VMuse()
    @ReactMethod fun Init() { vMuse.Init() }
    @ReactMethod fun StopSearch() { vMuse.StopSearch() }
    @ReactMethod fun StartSearch() { vMuse.StartSearch() }
    @ReactMethod fun RestartSearch() { vMuse.RestartSearch() }
    @ReactMethod fun Connect(museIndex: Int) { vMuse.Connect(museIndex) }
    @ReactMethod fun Disconnect() { vMuse.Disconnect() }
    @ReactMethod fun TogglePaused() { vMuse.TogglePaused() }
}