# react-native-libmuse
Bridge between libmuse and react-native. (used for my other project)

# Install

1) Run "npm install react-native-libmuse --save".  
2) Download and install libmuse from here: http://dev.choosemuse.com/android  
3) Copy "[libmuse install folder]/android/libs/libmuse_android.jar" into "[your project folder]/node_modules/react-native-libmuse/android/Libraries/".  

# Link module with your project

## Option A - automatic

#### With React Native 0.27+

```shell
react-native link react-native-libmuse
```

## Option B - manual

##### Android

- in `android/app/build.gradle`:

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-libmuse')
}
```

- in `android/settings.gradle`:

```diff
...
include ':app'
+ include ':react-native-libmuse'
+ project(':react-native-libmuse').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-libmuse/android')
```

##### With React Native 0.29+

- in `MainApplication.java`:

```diff
+ import com.v.LibMuse;

  public class MainApplication extends Application implements ReactApplication {
    //......

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
+         new LibMuse(),
          new MainReactPackage()
      );
    }

    ......
  }
```

# Usage

```
var LibMuse = require("react-native-libmuse");
LibMuse.Start();
//LibMuse.Refresh(); // calling this can improve the device-find speed, for some reason
DeviceEventEmitter.addListener("OnMuseListChange", args=> {
	var [museList] = args;
	if (museList.length)
		LibMuse.Connect();
});
DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
	var [type, dataStr] = args;
	var data = FromJSON(dataStr);
	console.log(`Type: ${type} Data: ${JSON.stringify(data)}`);
	// ex: "Type: eeg Data: [0.0,728.901098901099,998.0586080586081,1517.838827838828,NaN,NaN]"
});
```