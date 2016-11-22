/**
 * @providesModule react-native-libmuse
 */

var LibMuseLink = require("react-native").NativeModules.LibMuseLink;

var listeners = [];
module.exports = {
	Start: function () {
		LibMuseLink.Start();
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, data] = args;
			for (let listener of listeners)
				listener(type, data);
		});
	},
	Connect: function(museIndex = 0) {
		LibMuseLink.Connect(museIndex);
	},
	Disconnect: function () {
		LibMuseLink.Disconnect();
	},
	
	AddMuseDataListener: function(listenerFunc) {
		listeners.push(listenerFunc);
	},
};