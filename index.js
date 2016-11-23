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
		DeviceEventEmitter.addListener("OnMuseListChange", args=> {
			var [museList] = args;
			for (let listener of onMuseListChange_listeners)
				listener(museList);
		});
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, dataStr] = args;
			var data = FromJSON(dataStr);
			for (let listener of onReceiveMuseDataPacket_listeners)
				listener(type, data);
		});
	},
	Refresh: function() {
		LibMuse.Refresh();
	},
	Connect: function(museIndex = 0) {
		LibMuse.Connect(museIndex);
	},
	Disconnect: function () {
		LibMuse.Disconnect();
	},
	TogglePaused: function() {
		LibMuse.TogglePaused();
	},
	
	AddListener_OnReceiveMuseDataPacket: function(listenerFunc) {
		onReceiveMuseDataPacket_listeners.push(listenerFunc);
	},
	AddListener_OnMuseListChange: function(listenerFunc) {
		onMuseListChange_listeners.push(listenerFunc);
	}
};