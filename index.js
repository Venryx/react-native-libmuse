/**
 * @providesModule react-native-libmuse
 */

Object.defineProperty(exports,"__esModule",{value:true});
 
var {NativeModules, DeviceEventEmitter} = require("react-native");
var LibMuse = NativeModules.LibMuse;

var listeners_onReceiveMuseDataPacket = [];
var listeners_onChangeMuseConnectStatus = []
var listeners_onChangeMuseList = [];
//module.exports = {
exports.default = {
	Init: function () {
		LibMuse.Init();
		DeviceEventEmitter.addListener("OnChangeMuseList", args=> {
			var [museList] = args;
			for (let listener of listeners_onChangeMuseList)
				listener(museList);
		});
		DeviceEventEmitter.addListener("OnChangeMuseConnectStatus", args=> {
			var [status] = args;
			for (let listener of listeners_onChangeMuseConnectStatus)
				listener(status);
		});
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, dataStr] = args;
			//var data = FromJSON(dataStr);
			for (let listener of listeners_onReceiveMuseDataPacket)
				listener(type, dataStr);
		});
	},
	
	// searching
	StopSearch: function() {
		LibMuse.RestartSearch();
	},
	StartSearch: function() {
		LibMuse.StartSearch();
	},
	RestartSearch: function() {
		LibMuse.RestartSearch();
	},
	AddListener_OnChangeMuseList: function(listenerFunc) {
		listeners_onChangeMuseList.push(listenerFunc);
	},
	
	// connecting
	Connect: function(museIndex = 0) {
		LibMuse.Connect(museIndex);
	},
	Disconnect: function () {
		LibMuse.Disconnect();
	},
	AddListener_OnChangeMuseConnectStatus: function(listenerFunc) {
		listeners_onChangeMuseConnectStatus.push(listenerFunc);
	},
	
	// data-streaming
	TogglePaused: function() {
		LibMuse.TogglePaused();
	},
	AddListener_OnReceiveMuseDataPacket: function(listenerFunc) {
		listeners_onReceiveMuseDataPacket.push(listenerFunc);
	},
};