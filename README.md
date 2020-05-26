# react-native-libmuse

Bridge between Muse EEG headsets and custom Android apps. (with passthrough API for react-native)

NOTE: The NPM package hasn't been updated in a long time, so rather than "npm install ..." below, just clone this repo for the latest version for now.

# Install

1) Run "npm install react-native-libmuse --save".

### Link module with Android app

- in `android/app/build.gradle`:

```diff
android {
    defaultConfig {
+        ndk { abiFilters "armeabi-v7a" }
    }
}

dependencies {
    [...]
    compile "com.facebook.react:react-native:+"  // from node_modules
+   compile project(':react-native-libmuse')
}
```

- in `android/settings.gradle`:

```diff
[...]
include ':app'
+ include ':react-native-libmuse'
+ project(':react-native-libmuse').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-libmuse/android')
```

### Expose passthrough API for react-native (requires v0.29+)

- in `MainApplication.java`:

```diff
+ import com.v.LibMuse;

  public class MainApplication extends Application implements ReactApplication {
    [...]
    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
+         new LibMuse(),
          new MainReactPackage()
      );
    }
    [...]
  }
```

# Usage (Android API) [WIP]

```kotlin
import VMuse from v.LibMuse;

fun Start() {
    val vMuse = VMuse()
    vMuse.Init()
    vMuse.StartSearch()
    vMuse.AddEventListener("OnChangeMuseList") { args ->
        var museList = args[0] as WritableArray
        Log.i(TAG, "MuseListSize: ${museList.size()}");
        if (museList.size() > 0) {
            vMuse.Connect()
        }
    }
    vMuse.AddEventListener("OnChangeMuseConnectStatus") { args ->
        var status = args[0] as String
        Log.i(TAG, "Status: $status");
    }
    vMuse.AddEventListener("OnReceiveMuseDataPacket_Android") { args ->
        var packet = args[0] as VMuseDataPacket
        Log.i(TAG, "Type: ${packet.type} ChannelValues: ${packet.eegValues.contentToString()}");
        // ex: "Type: eeg ChannelValues: [0.0,728.901098901099,998.0586080586081,1517.838827838828,-1000000000,-1000000000]"
        // note that -1000000000 signifies "not a number", i.e. no data (communication channel doesn't support NaN)
    }
}
```

# Usage (react-native API)

```javascript
var LibMuse = require("react-native-libmuse");
LibMuse.Init();
LibMuse.StartSearch();
LibMuse.AddListener_OnChangeMuseList(museList=> {
    console.log(`MuseListSize: ${museList.length}`);
	if (museList.length) {
        LibMuse.Connect();
    }
});
LibMuse.AddListener_OnChangeMuseConnectStatus(status=> {
    console.log(`Status: ${status}`);
});
LibMuse.AddListener_OnReceiveMuseDataPacket(packet=> {
    console.log(packet);
    // ex: {eegValues: [0.0,728.901098901099,998.0586080586081,1517.838827838828,-1000000000,-1000000000]}
	// note that -1000000000 signifies "not a number", i.e. no data (communication channel doesn't support NaN)
});
```

# Troubleshooting

> I hit an error: "Duplicate class com.facebook.jni.CppException found in modules [...].jar"

You're most likely using another Facebook library like pytorch, which contains duplicates of the Facebook JNI classes defined in react-native. To fix this, add `excludeReactNative = true` to your app's `gradle.properties` file.