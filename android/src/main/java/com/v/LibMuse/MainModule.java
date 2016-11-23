package com.v.LibMuse;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.AnnotationData;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.MessageType;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConfiguration;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseFileReader;
import com.choosemuse.libmuse.MuseFileWriter;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;
import com.choosemuse.libmuse.MuseVersion;
import com.choosemuse.libmuse.Result;
import com.choosemuse.libmuse.ResultLevel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.v.LibMuse.LibMuse.mainActivity;

/**
 * This example will illustrate how to connect to a Muse headband,
 * register for and receive EEG data and disconnect from the headband.
 * Saving EEG data to a .muse file is also covered.
 * <p>
 * For instructions on how to pair your headband with your Android device
 * please see:
 * http://developer.choosemuse.com/hardware-firmware/bluetooth-connectivity/developer-sdk-bluetooth-connectivity-2
 * <p>
 * Usage instructions:
 * 1. Pair your headband if necessary.
 * 2. Run this project.
 * 3. Turn on the Muse headband.
 * 4. Press "Refresh". It should display all paired Muses in the Spinner drop down at the
 * top of the screen.  It may take a few seconds for the headband to be detected.
 * 5. Select the headband you want to connect to and press "Connect".
 * 6. You should see EEG and accelerometer data as well as connection status,
 * version information and relative alpha values appear on the screen.
 * 7. You can pause/resume data transmission with the button at the bottom of the screen.
 * 8. To disconnect from the headband, press "Disconnect"
 */
class ListenerService {

	/**
	 * Tag used for logging purposes.
	 */
	public static final String TAG = "TestLibMuseAndroid";

	/**
	 * The MuseManager is how you detect Muse headbands and receive notifications
	 * when the list of available headbands changes.
	 */
	public MuseManagerAndroid manager;

	/**
	 * A Muse refers to a Muse headband.  Use this to connect/disconnect from the
	 * headband, register listeners to receive EEG data and get headband
	 * configuration and version information.
	 */
	private Muse muse;

	/**
	 * The ConnectionListener will be notified whenever there is a change in
	 * the connection state of a headband, for example when the headband connects
	 * or disconnects.
	 * <p>
	 * Note that ConnectionListener is an inner class at the bottom of this file
	 * that extends MuseConnectionListener.
	 */
	private ConnectionListener connectionListener;

	/**
	 * We will be updating the UI using a handler instead of in packet handlers because
	 * packets come in at a very high frequency and it only makes sense to update the UI
	 * at about 60fps. The update functions do some string allocation, so this reduces our memory
	 * footprint and makes GC pauses less frequent/noticeable.
	 */
	private final Handler handler = new Handler();

	/**
	 * In the UI, the list of Muses you can connect to is displayed in a Spinner object for this example.
	 * This spinner adapter contains the MAC addresses of all of the headbands we have discovered.
	 */
	private ArrayAdapter<String> spinnerAdapter;

	/**
	 * It is possible to pause the data transmission from the headband.  This boolean tracks whether
	 * or not the data transmission is enabled as we allow the user to pause transmission in the UI.
	 */
	private boolean dataTransmission = true;

	/**
	 * To save data to a file, you should use a MuseFileWriter.  The MuseFileWriter knows how to
	 * serialize the data packets received from the headband into a compact binary format.
	 * To read the file back, you would use a MuseFileReader.
	 */
	private final AtomicReference<MuseFileWriter> fileWriter = new AtomicReference<>();

	/**
	 * We don't want file operations to slow down the UI, so we will defer those file operations
	 * to a handler on a separate thread.
	 */
	private final AtomicReference<Handler> fileHandler = new AtomicReference<>();

	//--------------------------------------
	// Lifecycle / Connection code

	public void Start() {
		//assert(manager != null);
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

		WeakReference<ListenerService> weakActivity = new WeakReference<ListenerService>(this);
		// Register a listener to receive connection state changes.
		connectionListener = new ConnectionListener(weakActivity);

		// Muse 2016 (MU-02) headbands use Bluetooth Low Energy technology to
		// simplify the connection process.  This requires access to the COARSE_LOCATION
		// or FINE_LOCATION permissions.  Make sure we have these permissions before
		// proceeding.
		ensurePermissions();
	}

	// make-so: root application calls into this library and tells to stop listening
	/*protected void onPause() {
		super.onPause();
		// It is important to call stopListening when the Activity is paused
		// to avoid a resource leak from the LibMuse library.
		manager.stopListening();
	}*/

	public void Refresh() {
		// The user has pressed the "Refresh" button.
		// Start listening for nearby or paired Muse headbands. We call stopListening
		// first to make sure startListening will clear the list of headbands and start fresh.
		manager.stopListening();
		manager.startListening();
	}

	public void Connect(int museIndex) {
		// The user has pressed the "Connect" button to connect to
		// the headband in the spinner.

		// Listening is an expensive operation, so now that we know
		// which headband the user wants to connect to we can stop
		// listening for other headbands.
		manager.stopListening();

		List<Muse> availableMuses = manager.getMuses();
		// Cache the Muse that the user has selected.
		muse = availableMuses.get(museIndex);
		// Unregister all prior listeners and register our data listener to
		// receive the MuseDataPacketTypes we are interested in.  If you do
		// not register a listener for a particular data type, you will not
		// receive data packets of that type.
		muse.unregisterAllListeners();
		muse.registerConnectionListener(connectionListener);

		// Initiate a connection to the headband and stream the data asynchronously.
		muse.runAsynchronously();
	}

	public void Disconnect() {
		// The user has pressed the "Disconnect" button.
		// Disconnect from the selected Muse.
		if (muse != null) {
			muse.disconnect(false);
		}
	}

	public void TogglePaused() {
		// The user has pressed the "Pause/Resume" button to either pause or
		// resume data transmission.  Toggle the state and pause or resume the
		// transmission on the headband.
		if (muse != null) {
			dataTransmission = !dataTransmission;
			muse.enableDataTransmission(dataTransmission);
		}
	}

	void RegisterDataListener(MuseDataListener listener) {
		muse.registerDataListener(listener, MuseDataPacketType.EEG);
		muse.registerDataListener(listener, MuseDataPacketType.ALPHA_RELATIVE);
		muse.registerDataListener(listener, MuseDataPacketType.ACCELEROMETER);
		muse.registerDataListener(listener, MuseDataPacketType.BATTERY);
		muse.registerDataListener(listener, MuseDataPacketType.DRL_REF);
		muse.registerDataListener(listener, MuseDataPacketType.QUANTIZATION);
	}

	//--------------------------------------
	// Permissions

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
	private void ensurePermissions() {
		if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// We don't have the ACCESS_COARSE_LOCATION permission so create the dialogs asking
			// the user to grant us the permission.

			// This is the context dialog which explains to the user the reason we are requesting
			// this permission.  When the user presses the positive (I Understand) button, the
			// standard Android permission dialog will be displayed (as defined in the button
			// listener above).
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
	}


	/**
	 * You will receive a callback to this method each time there is a change to the
	 * connection state of one of the headbands.
	 *
	 * @param p    A packet containing the current and prior connection states
	 * @param muse The headband whose state changed.
	 */
	public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

		final ConnectionState current = p.getCurrentConnectionState();

		// Format a message to show the change of connection state in the UI.
		final String status = p.getPreviousConnectionState() + " -> " + current;
		Log.i(TAG, status);

		if (current == ConnectionState.DISCONNECTED) {
			Log.i(TAG, "Muse disconnected:" + muse.getName());
			// Save the data file once streaming has stopped.
			saveFile();
			// We have disconnected from the headband, so set our cached copy to null.
			this.muse = null;
		}
	}

	/**
	 * You will receive a callback to this method each time an artifact packet is generated if you
	 * have registered for the ARTIFACTS data type.  MuseArtifactPackets are generated when
	 * eye blinks are detected, the jaw is clenched and when the headband is put on or removed.
	 *
	 * @param p    The artifact packet with the data from the headband.
	 * @param muse The headband that sent the information.
	 */
	public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
	}

	/**
	 * Flushes all the data to the file and closes the file writer.
	 */
	private void saveFile() {
		Handler h = fileHandler.get();
		if (h != null) {
			h.post(new Runnable() {
				@Override
				public void run() {
					MuseFileWriter w = fileWriter.get();
					// Annotation strings can be added to the file to
					// give context as to what is happening at that point in
					// time.  An annotation can be an arbitrary string or
					// may include additional AnnotationData.
					w.addAnnotationString(0, "Disconnected");
					w.flush();
					w.close();
				}
			});
		}
	}


	class ConnectionListener extends MuseConnectionListener {
		final WeakReference<ListenerService> activityRef;
		ConnectionListener(final WeakReference<ListenerService> activityRef) {
			this.activityRef = activityRef;
		}

		@Override
		public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
			activityRef.get().receiveMuseConnectionPacket(p, muse);
		}
	}
}

class MainModule extends ReactContextBaseJavaModule {
	static MainModule main;

	ReactApplicationContext reactContext;

	public MainModule(ReactApplicationContext reactContext) {
		super(reactContext);
		main = this;
		this.reactContext = reactContext;
	}
	DeviceEventManagerModule.RCTDeviceEventEmitter jsModuleEventEmitter;

	@Override
	public String getName() {
		return "LibMuse";
	}
	
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

		/*WritableMap pack = Arguments.createMap();
		pack.putArray("args", argsList);*/

		DeviceEventManagerModule.RCTDeviceEventEmitter jsModuleEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
		jsModuleEventEmitter.emit(eventName, argsList);
	}

	ListenerService service;
	DataListener listener = new DataListener();

    @ReactMethod public void Start() {
		service = new ListenerService();
		//Activity mainActivity = GetActivity();
		service.Start();

		// Register a listener to receive notifications of what Muse headbands
		// we can connect to.
		service.manager.setMuseListener(new MuseL());
    }
	class MuseL extends MuseListener {
		@Override public void museListChanged() {
			MainModule.main.OnMuseListChanged();
		}
	}
	public void OnMuseListChanged() {
		List<Muse> muses = service.manager.getMuses();
		WritableArray museList = Arguments.createArray();
		for (Muse muse : muses) {
			WritableMap museInfo = Arguments.createMap();
			museInfo.putString("name", muse.getName());
			museInfo.putString("macAddress", muse.getMacAddress());
			museInfo.putDouble("lastDiscoveredTime", muse.getLastDiscoveredTime());
			museInfo.putDouble("rssi", muse.getRssi());
			museList.pushMap(museInfo);
		}
		SendEvent("OnMuseListChange", museList);
	}

	@ReactMethod public void Refresh() {
		service.Refresh();
	}
	@ReactMethod public void Connect(int museIndex) {
		service.Connect(museIndex);
		service.RegisterDataListener(listener);
	}
	@ReactMethod public void Disconnect() {
		service.Disconnect();
	}
	@ReactMethod public void TogglePaused() {
		service.TogglePaused();
	}
	
	class DataListener extends MuseDataListener {
        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
			// valuesSize returns the number of data values contained in the packet.
			final long n = p.valuesSize();
			MuseDataPacketType packetType = p.packetType();
			
			String type = null;
			//final WritableArray data = Arguments.createArray();
			//final double[] data;
			List<Double> data = new ArrayList<>();
			if (packetType == MuseDataPacketType.EEG) {
				type = "eeg";
				//data.pushDouble(p.getEegChannelValue(Eeg.EEG1));
				//data = new double[6];
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
				Log.i(ListenerService.TAG, "Error: " + ex);
			}
		}

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
        }
    }
}