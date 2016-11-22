package com.v.LibMuse;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

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
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

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
class ListenerActivity extends Activity {

	/**
	 * Tag used for logging purposes.
	 */
	private final String TAG = "TestLibMuseAndroid";

	/**
	 * The MuseManager is how you detect Muse headbands and receive notifications
	 * when the list of available headbands changes.
	 */
	private MuseManagerAndroid manager;

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
	 * Data comes in from the headband at a very fast rate; 220Hz, 256Hz or 500Hz,
	 * depending on the type of headband and the preset configuration.  We buffer the
	 * data that is read until we can update the UI.
	 * <p>
	 * The stale flags indicate whether or not new data has been received and the buffers
	 * hold the values of the last data packet received.  We are displaying the EEG, ALPHA_RELATIVE
	 * and ACCELEROMETER values in this example.
	 * <p>
	 * Note: the array lengths of the buffers are taken from the comments in
	 * MuseDataPacketType, which specify 3 values for accelerometer and 6
	 * values for EEG and EEG-derived packets.
	 */
	private final double[] eegBuffer = new double[6];
	private boolean eegStale;
	private final double[] alphaBuffer = new double[6];
	private boolean alphaStale;
	private final double[] accelBuffer = new double[3];
	private boolean accelStale;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// We need to set the context on MuseManagerAndroid before we can do anything.
		// This must come before other LibMuse API calls as it also loads the library.
		manager = MuseManagerAndroid.getInstance();
		manager.setContext(this);

		Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());

		WeakReference<ListenerActivity> weakActivity =
				new WeakReference<ListenerActivity>(this);
		// Register a listener to receive connection state changes.
		connectionListener = new ConnectionListener(weakActivity);
		// Register a listener to receive notifications of what Muse headbands
		// we can connect to.
		manager.setMuseListener(new MuseL(weakActivity));

		// Muse 2016 (MU-02) headbands use Bluetooth Low Energy technology to
		// simplify the connection process.  This requires access to the COARSE_LOCATION
		// or FINE_LOCATION permissions.  Make sure we have these permissions before
		// proceeding.
		ensurePermissions();

		// Start up a thread for asynchronous file operations.
		// This is only needed if you want to do File I/O.
		fileThread.start();
	}

	protected void onPause() {
		super.onPause();
		// It is important to call stopListening when the Activity is paused
		// to avoid a resource leak from the LibMuse library.
		manager.stopListening();
	}

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
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// We don't have the ACCESS_COARSE_LOCATION permission so create the dialogs asking
			// the user to grant us the permission.

			// This is the context dialog which explains to the user the reason we are requesting
			// this permission.  When the user presses the positive (I Understand) button, the
			// standard Android permission dialog will be displayed (as defined in the button
			// listener above).
			AlertDialog introDialog = new AlertDialog.Builder(this)
					.setTitle("Requesting permissions")
					.setMessage("Location-services permission needed for Bluetooth connection to work.")
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							ActivityCompat.requestPermissions(ListenerActivity.this,
									new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
									0);
						}
					})
					.create();
			introDialog.show();
		}
	}


	//--------------------------------------
	// Listeners

	/**
	 * You will receive a callback to this method each time a headband is discovered.
	 * In this example, we update the spinner with the MAC address of the headband.
	 */
	public void museListChanged() {
		final List<Muse> list = manager.getMuses();
		spinnerAdapter.clear();
		for (Muse m : list) {
			spinnerAdapter.add(m.getName() + " - " + m.getMacAddress());
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


	//--------------------------------------
	// File I/O

	/**
	 * We don't want to block the UI thread while we write to a file, so the file
	 * writing is moved to a separate thread.
	 */
	private final Thread fileThread = new Thread() {
		@Override
		public void run() {
			Looper.prepare();
			fileHandler.set(new Handler());
			final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			final File file = new File(dir, "new_muse_file.muse");
			// MuseFileWriter will append to an existing file.
			// In this case, we want to start fresh so the file
			// if it exists.
			if (file.exists()) {
				file.delete();
			}
			Log.i(TAG, "Writing data to: " + file.getAbsolutePath());
			fileWriter.set(MuseFileFactory.getMuseFileWriter(file));
			Looper.loop();
		}
	};

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

	/**
	 * Reads the provided .muse file and prints the data to the logcat.
	 *
	 * @param name The name of the file to read.  The file in this example
	 *             is assumed to be in the Environment.DIRECTORY_DOWNLOADS
	 *             directory.
	 */
	private void playMuseFile(String name) {

		File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		File file = new File(dir, name);

		final String tag = "Muse File Reader";

		if (!file.exists()) {
			Log.w(tag, "file doesn't exist");
			return;
		}

		MuseFileReader fileReader = MuseFileFactory.getMuseFileReader(file);

		// Loop through each message in the file.  gotoNextMessage will read the next message
		// and return the result of the read operation as a Result.
		Result res = fileReader.gotoNextMessage();
		while (res.getLevel() == ResultLevel.R_INFO && !res.getInfo().contains("EOF")) {

			MessageType type = fileReader.getMessageType();
			int id = fileReader.getMessageId();
			long timestamp = fileReader.getMessageTimestamp();

			Log.i(tag, "type: " + type.toString() +
					" id: " + Integer.toString(id) +
					" timestamp: " + String.valueOf(timestamp));

			switch (type) {
				// EEG messages contain raw EEG data or DRL/REF data.
				// EEG derived packets like ALPHA_RELATIVE and artifact packets
				// are stored as MUSE_ELEMENTS messages.
				case EEG:
				case BATTERY:
				case ACCELEROMETER:
				case QUANTIZATION:
				case GYRO:
				case MUSE_ELEMENTS:
					MuseDataPacket packet = fileReader.getDataPacket();
					Log.i(tag, "data packet: " + packet.packetType().toString());
					break;
				case VERSION:
					MuseVersion version = fileReader.getVersion();
					Log.i(tag, "version" + version.getFirmwareType());
					break;
				case CONFIGURATION:
					MuseConfiguration config = fileReader.getConfiguration();
					Log.i(tag, "config" + config.getBluetoothMac());
					break;
				case ANNOTATION:
					AnnotationData annotation = fileReader.getAnnotation();
					Log.i(tag, "annotation" + annotation.getData());
					break;
				default:
					break;
			}

			// Read the next message.
			res = fileReader.gotoNextMessage();
		}
	}

	//--------------------------------------
	// Listener translators
	//
	// Each of these classes extend from the appropriate listener and contain a weak reference
	// to the activity.  Each class simply forwards the messages it receives back to the Activity.
	class MuseL extends MuseListener {
		final WeakReference<ListenerActivity> activityRef;

		MuseL(final WeakReference<ListenerActivity> activityRef) {
			this.activityRef = activityRef;
		}

		@Override
		public void museListChanged() {
			activityRef.get().museListChanged();
		}
	}

	class ConnectionListener extends MuseConnectionListener {
		final WeakReference<ListenerActivity> activityRef;

		ConnectionListener(final WeakReference<ListenerActivity> activityRef) {
			this.activityRef = activityRef;
		}

		@Override
		public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
			activityRef.get().receiveMuseConnectionPacket(p, muse);
		}
	}
}

public class MainModule extends ReactContextBaseJavaModule {
	ReactApplicationContext reactContext;

	public MainModule(ReactApplicationContext reactContext) {
	super(reactContext);
		this.reactContext = reactContext;
	}

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

		DeviceEventManagerModule.RCTDeviceEventEmitter jsModuleEventEmitter = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
		jsModuleEventEmitter.emit(eventName, argsList);
	}
	

    //--------------------------------------
    // Lifecycle / Connection code
	
	ListenerActivity activity;
	DataListener listener = new DataListener();
    public void Start() {
        // todo: create activity
		activity = new ListenerActivity();
		activity.RegisterDataListener(listener);
    }

	public void Refresh() {
		activity.Refresh();
	}
	public void Connect(int museIndex) {
		activity.Connect(museIndex);
	}
	public void Disconnect() {
		activity.Disconnect();
	}
	public void TogglePaused() {
		activity.TogglePaused();
	}
	
	class DataListener extends MuseDataListener {
        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
			// valuesSize returns the number of data values contained in the packet.
			final long n = p.valuesSize();
			MuseDataPacketType packetType = p.packetType();
			
			String type = null;
			final WritableArray data = Arguments.createArray();
			if (packetType == MuseDataPacketType.EEG) {
				type = "eeg";
				data.pushDouble(p.getEegChannelValue(Eeg.EEG1));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG2));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG3));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG4));
				data.pushDouble(p.getEegChannelValue(Eeg.AUX_LEFT));
				data.pushDouble(p.getEegChannelValue(Eeg.AUX_RIGHT));
			}
			else if (packetType == MuseDataPacketType.ACCELEROMETER) {
				type = "accelerometer";
				data.pushDouble(p.getAccelerometerValue(Accelerometer.X));
				data.pushDouble(p.getAccelerometerValue(Accelerometer.Y));
				data.pushDouble(p.getAccelerometerValue(Accelerometer.Z));
			}
			else if (packetType == MuseDataPacketType.ALPHA_RELATIVE) {
				type = "alpha";
				data.pushDouble(p.getEegChannelValue(Eeg.EEG1));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG2));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG3));
				data.pushDouble(p.getEegChannelValue(Eeg.EEG4));
				data.pushDouble(p.getEegChannelValue(Eeg.AUX_LEFT));
				data.pushDouble(p.getEegChannelValue(Eeg.AUX_RIGHT));
			}
			else // currently we just ignore other packet types
				return;

			// packet-data value-count should match the number expected
			assert(data.size() == n);
			
            SendEvent("OnReceiveMuseDataPacket", type, data);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
        }
    }
}