/**
 * @providesModule react-native-libmuse
 */

Object.defineProperty(exports,"__esModule",{value:true});
 
var {NativeModules, DeviceEventEmitter} = require("react-native");
var LibMuse = NativeModules.LibMuse;

var listeners = [];
//module.exports = {
exports.default = {
	Start: function () {
		LibMuse.Start();
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, data] = args;
			for (let listener of listeners)
				listener(type, data);
		});
	},
	Connect: function(museIndex = 0) {
		LibMuse.Connect(museIndex);
	},
	Disconnect: function () {
		LibMuse.Disconnect();
	},
	
	AddMuseDataListener: function(listenerFunc) {
		listeners.push(listenerFunc);
	},
};