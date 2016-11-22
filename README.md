# react-native-libmuse
Bridge between libmuse and react-native. (used for my other project)

## Install

1) Run "npm install react-native-libmuse --save".  
2) Download and install libmuse from here: http://dev.choosemuse.com/android  
3) Copy "[libmuse install folder]/android/libs/libmuse_android.jar" into "[your project folder]/node_modules/react-native-libmuse/Libraries/".  

## Automatically link

#### With React Native 0.27+

```shell
react-native link react-native-libmuselink
```

## Manually link

##### Android

- in `android/app/build.gradle`:

```diff
dependencies {
    ...
    compile "com.facebook.react:react-native:+"  // From node_modules
+   compile project(':react-native-libmuselink')
}
```

- in `android/settings.gradle`:

```diff
...
include ':app'
+ include ':react-native-libmuselink'
+ project(':react-native-libmuselink').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-libmuselink/android')
```

##### With React Native 0.29+

- in `MainApplication.java`:

```diff
+ import com.v.LibMuseLink;

  public class MainApplication extends Application implements ReactApplication {
    //......

    @Override
    protected List<ReactPackage> getPackages() {
      return Arrays.<ReactPackage>asList(
+         new LibMuseLink(),
          new MainReactPackage()
      );
    }

    ......
  }
```

# Usage

```
var LibMuseLink = require("react-native-libmuselink");
link.Start();
link.Connect();
```