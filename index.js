/**
 * @providesModule react-native-libmuse
 */

Object.defineProperty(exports,"__esModule",{value:true});
 
var {NativeModules, DeviceEventEmitter} = require("react-native");
var LibMuse = NativeModules.LibMuse;

var onReceiveMuseDataPacket_listeners = [];
var onMuseListChange_listeners = [];
//module.exports = {
exports.default = {
	Start: function () {
		LibMuse.Start();
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, data] = args;
			for (let listener of onReceiveMuseDataPacket_listeners)
				listener(type, data);
		});
		DeviceEventEmitter.addListener("OnMuseListChange", args=> {
			var [type, data] = args;
			for (let listener of onMuseListChange_listeners)
				listener(type, data);
		});
	},
	Connect: function(museIndex = 0) {
		LibMuse.Connect(museIndex);
	},
	Disconnect: function () {
		LibMuse.Disconnect();
	},
	
	AddListener_OnReceiveMuseDataPacket: function(listenerFunc) {
		onReceiveMuseDataPacket_listeners.push(listenerFunc);
	},
	AddListener_OnMuseListChange: function(listenerFunc) {
		onMuseListChange_listeners.push(listenerFunc);
	}
};