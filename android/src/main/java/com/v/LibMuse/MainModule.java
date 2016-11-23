package com.v.LibMuse;

import java.util.ArrayList;
import java.util.List;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.v.LibMuse.LibMuse.mainActivity;

class MainModule extends ReactContextBaseJavaModule {
	// tag used for logging purposes.
	static final String TAG = "TestLibMuseAndroid";

	public MainModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
	}
	ReactApplicationContext reactContext;

	@Override
	public String getName() { return "LibMuse"; }
	
	public void SendEvent(String eventName, Object... args) {
		WritableArray argsList = Arguments.createArray();
		for (Object arg : args) {
			if (arg == null)
				argsList.pushNull();
			else if (arg instanceof Boolean)
				argsList.pushBoolean((Boolean)arg);
			else if (arg instanceof Integer)
				argsList.pushInt((Integer)arg);
			else if (arg instanceof Double)
				argsList.pushDouble((Double)arg);
			else if (arg instanceof String)
				argsList.pushString((String)arg);
			else if (arg instanceof WritableArray)
				argsList.pushArray((WritableArray)arg);
			else {
				//Assert(arg instanceof WritableMap, "Event args must be one of: WritableArray, Boolean")
				if (!(arg instanceof WritableMap))
					throw new RuntimeException("Event args must be one of: Boolean, Integer, Double, String, WritableArray, WritableMap");
				argsList.pushMap((WritableMap)arg);
			}
		}

		DeviceEventManagerModule.RCTDeviceEventEmitter jsModuleEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
		jsModuleEventEmitter.emit(eventName, argsList);
	}

	// the MuseManager is how you detect Muse headbands and receive notifications when the list of available headbands changes
	public MuseManagerAndroid manager;

    @ReactMethod public void Init() {
		if (mainActivity == null)
			throw new RuntimeException("LibMuse.mainActivity not set. (set it in your main-activity's constructor)");

		// We need to set the context on MuseManagerAndroid before we can do anything.
		// This must come before other LibMuse API calls as it also loads the library.
		try {
			manager = MuseManagerAndroid.getInstance();
			manager.setContext(mainActivity);
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to start muse-manager: " + ex);
		}

		Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());

		// Muse 2016 (MU-02) headbands use Bluetooth Low Energy technology to simplify the connection process.
		// This requires the COARSE_LOCATION or FINE_LOCATION permissions. Make sure we have these permissions before proceeding.
		EnsurePermissions();

		AddMuseListListener();
    }

	/**
	 * The ACCESS_COARSE_LOCATION permission is required to use the
	 * Bluetooth Low Energy library and must be requested at runtime for Android 6.0+
	 * On an Android 6.0 device, the following code will display 2 dialogs,
	 * one to provide context and the second to request the permission.
	 * On an Android device running an earlier version, nothing is displayed
	 * as the permission is granted from the manifest.
	 * <p>
	 * If the permission is not granted, then Muse 2016 (MU-02) headbands will
	 * not be discovered and a SecurityException will be thrown.
	 */
	private void EnsurePermissions() {
		if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) return;

		// We don't have the ACCESS_COARSE_LOCATION permission, so create the dialogs asking the user to grant it.

		// This is the context dialog which explains to the user the reason we are requesting
		// this permission.  When the user presses the positive (I Understand) button, the
		// standard Android permission dialog will be displayed (as defined in the button listener above).
		AlertDialog introDialog = new AlertDialog.Builder(mainActivity)
			.setTitle("Requesting permissions")
			.setMessage("Location-services permission needed for Bluetooth connection to work.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
				}
			})
			.create();
		introDialog.show();
	}

	// make-so: root application calls this on-pause and on-exit
	@ReactMethod public void StopSearch() {
		manager.stopListening();
	}
	@ReactMethod void StartSearch() {
		manager.startListening();
	}
	@ReactMethod public void RestartSearch() {
		StopSearch(); // clear the list of headbands and start fresh
		StartSearch();
	}
	void AddMuseListListener() {
		// Register a listener to receive notifications of what Muse headbands we can connect to.
		manager.setMuseListener(new MuseListener() {
			@Override public void museListChanged() {
				List<Muse> muses = manager.getMuses();
				WritableArray museList = Arguments.createArray();
				for (Muse muse : muses) {
					WritableMap museInfo = Arguments.createMap();
					museInfo.putString("name", muse.getName());
					museInfo.putString("macAddress", muse.getMacAddress());
					museInfo.putDouble("lastDiscoveredTime", muse.getLastDiscoveredTime());
					museInfo.putDouble("rssi", muse.getRssi());
					museList.pushMap(museInfo);
				}
				SendEvent("OnChangeMuseList", museList);
			}
		});
	}

	Muse muse;
	@ReactMethod public void Connect(int museIndex) {
		// Listening is expensive, so now that we know which headband the user wants to connect to, we can stop listening for other headbands.
		StopSearch();

		List<Muse> availableMuses = manager.getMuses();
		// Cache the Muse that the user has selected.
		muse = availableMuses.get(museIndex);

		// Unregister all prior listeners and register our data listener to receive the MuseDataPacketTypes we are interested in.
		// If you do not register a listener for a particular data type, you will not receive data packets of that type.
		muse.unregisterAllListeners();
		AddConnectionListener();

		// Initiate a connection to the headband and stream the data asynchronously.
		muse.runAsynchronously();
		AddDataListener();
	}
	@ReactMethod public void Disconnect() {
		if (muse == null) return;
		muse.disconnect(false);
	}

	void AddConnectionListener() {
		muse.registerConnectionListener(new MuseConnectionListener() {
			@Override public void receiveMuseConnectionPacket(MuseConnectionPacket packet, Muse muse) {
				final ConnectionState current = packet.getCurrentConnectionState();

				// Format a message to show the change of connection state in the UI.
				final String status = packet.getPreviousConnectionState() + " -> " + current;
				Log.i(TAG, status);

				if (current == ConnectionState.CONNECTED) {
					Log.i(TAG, "Muse connected: " + muse.getName());
				}
				if (current == ConnectionState.DISCONNECTED) {
					Log.i(TAG, "Muse disconnected: " + muse.getName());
					// We have disconnected from the headband, so set our cached copy to null.
					MainModule.this.muse = null;
				}

				SendEvent("OnChangeMuseConnectStatus", current.name().toLowerCase());
			}
		});
	}

	void AddDataListener() {
		RegisterDataListener(new MuseDataListener() {
			@Override
			public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
				// valuesSize returns the number of data values contained in the packet.
				final long n = p.valuesSize();
				MuseDataPacketType packetType = p.packetType();

				String type;
				List<Double> data = new ArrayList<>();
				if (packetType == MuseDataPacketType.EEG) {
					type = "eeg";
					data.add(p.getEegChannelValue(Eeg.EEG1));
					data.add(p.getEegChannelValue(Eeg.EEG2));
					data.add(p.getEegChannelValue(Eeg.EEG3));
					data.add(p.getEegChannelValue(Eeg.EEG4));
					data.add(p.getEegChannelValue(Eeg.AUX_LEFT));
					data.add(p.getEegChannelValue(Eeg.AUX_RIGHT));
				}
				else if (packetType == MuseDataPacketType.ACCELEROMETER) {
					type = "accelerometer";
					data.add(p.getAccelerometerValue(Accelerometer.X));
					data.add(p.getAccelerometerValue(Accelerometer.Y));
					data.add(p.getAccelerometerValue(Accelerometer.Z));
				}
				else if (packetType == MuseDataPacketType.ALPHA_RELATIVE) {
					type = "alpha";
					data.add(p.getEegChannelValue(Eeg.EEG1));
					data.add(p.getEegChannelValue(Eeg.EEG2));
					data.add(p.getEegChannelValue(Eeg.EEG3));
					data.add(p.getEegChannelValue(Eeg.EEG4));
					data.add(p.getEegChannelValue(Eeg.AUX_LEFT));
					data.add(p.getEegChannelValue(Eeg.AUX_RIGHT));
				}
				else // currently we just ignore other packet types
					return;

				// note: double NaN cannot be serialized by normal react event-emit system
				// 		that's one of the reasons we're using Json serialization
				//		(the JSON serializer rejects NaN by default too, but can be corrected with call below)
				try {
					GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.serializeSpecialFloatingPointValues();
					Gson gson = gsonBuilder.create();

					String dataStr = gson.toJson(data);
					//Log.i(ListenerService.TAG, "Sent JSON: " + dataStr);
					SendEvent("OnReceiveMuseDataPacket", type, dataStr);
				} catch (Throwable ex) {
					Log.i(TAG, "Error: " + ex);
				}
			}

			@Override
			public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {}
		});
	}
	void RegisterDataListener(MuseDataListener listener) {
		muse.registerDataListener(listener, MuseDataPacketType.EEG);
		muse.registerDataListener(listener, MuseDataPacketType.ALPHA_RELATIVE);
		muse.registerDataListener(listener, MuseDataPacketType.ACCELEROMETER);
		muse.registerDataListener(listener, MuseDataPacketType.BATTERY);
		muse.registerDataListener(listener, MuseDataPacketType.DRL_REF);
		muse.registerDataListener(listener, MuseDataPacketType.QUANTIZATION);
	}

	// whether the data-transmission from the headband is enabled
	private boolean transmissionEnabled = true;
	@ReactMethod public void TogglePaused() {
		if (muse == null) return;
		transmissionEnabled = !transmissionEnabled;
		muse.enableDataTransmission(transmissionEnabled);
	}
}