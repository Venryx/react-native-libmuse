# react-native-libmuse
Bridge between libmuse and react-native. (used for my other project)

# Installation

1) Run "npm install react-native-libmuse --save".  
2) Run "react-native link react-native-libmuse".
3) Download and install libmuse from here: http://dev.choosemuse.com/android  
4) Copy "[libmuse install folder]/android/libs/libmuse_android.jar" into "[your project folder]/node_modules/react-native-libmuse/Libraries/".  

# Usage

```
var LibMuseLink = require('react-native-libmuselink');

var link = new LibMuseLink();
link.Start();
link.Connect();
```