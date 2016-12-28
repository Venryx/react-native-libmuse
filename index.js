/**
 * @providesModule react-native-libmuse
 */

Object.defineProperty(exports, "__esModule", {value: true});
 
var {NativeModules, DeviceEventEmitter} = require("react-native");
var LibMuse = NativeModules.LibMuse;
var BackgroundTimer = require("react-native-background-timer");

class LibMuseJS {
	listeners_onReceiveMuseDataPacket = [];
	listeners_onChangeMuseConnectStatus = []
	listeners_onChangeMuseList = [];

	// set to -1, to disable reconnect-attempt helper
	// (reconnection will still be attempted, by base library, but not
	//		our disconnect-then-reconnect operation which helps increases the chances)
	reconnectAttemptInterval = 10;

	reconnectAttemptTimerID = -1;
	StartReconnectAttemptTimer() {
		// ensure we don't have two attempt-timers running at once
		this.StopReconnectAttemptTimer();
		// start timer
		this.reconnectAttemptTimerID = BackgroundTimer.setInterval(()=> {
			// ensure still disconnected (it might be in the process of connecting)
			if (this.connectionStatus == "disconnected") 
				this.RestartSearch();
		}, this.reconnectAttemptInterval);
	}
	StopReconnectAttemptTimer() {
		if (this.reconnectAttemptTimerID == -1) return;
		// stop timer
		BackgroundTimer.clearInterval(this.reconnectAttemptTimerID);
		this.reconnectAttemptTimerID = -1;
	}

	connectionStatus = "disconnected";

	Init() {
		LibMuse.Init();
		DeviceEventEmitter.addListener("OnChangeMuseList", args=> {
			var [museList] = args;
			for (let listener of this.listeners_onChangeMuseList)
				listener(museList);
		});
		DeviceEventEmitter.addListener("OnChangeMuseConnectStatus", args=> {
			var [status] = args;
			for (let listener of this.listeners_onChangeMuseConnectStatus)
				listener(status);

			this.connectionStatus = status;
			if (status == "connected")
				this.StopReconnectAttemptTimer();
			else if (status == "disconnected" && this.reconnectAttemptInterval != -1)
				this.StartReconnectAttemptTimer();
		});
		/*DeviceEventEmitter.addListener("OnReceiveMuseDataPacket", args=> {
			var [type, channelValues] = args;
			for (let listener of listeners_onReceiveMuseDataPacket)
				listener(type, channelValues);
		});*/
		DeviceEventEmitter.addListener("OnReceiveMuseDataPacketSet", args=> {
			var [packets] = args;
			for (let packet of packets) {
				for (let listener of this.listeners_onReceiveMuseDataPacket)
					listener(packet);
			}
		});
	}
	
	// searching
	StopSearch() {
		LibMuse.StopSearch();
	}
	StartSearch(reconnectAttemptInterval) {
		LibMuse.StartSearch();
	}
	RestartSearch(reconnectAttemptInterval) {
		LibMuse.RestartSearch();
	}
	AddListener_OnChangeMuseList(listenerFunc) {
		this.listeners_onChangeMuseList.push(listenerFunc);
	}
	
	// connecting
	Connect(museIndex = 0) {
		LibMuse.Connect(museIndex);
	}
	Disconnect() {
		LibMuse.Disconnect();
	}
	AddListener_OnChangeMuseConnectStatus(listenerFunc) {
		this.listeners_onChangeMuseConnectStatus.push(listenerFunc);
	}
	
	// data-streaming
	TogglePaused() {
		LibMuse.TogglePaused();
	}
	AddListener_OnReceiveMuseDataPacket(listenerFunc) {
		this.listeners_onReceiveMuseDataPacket.push(listenerFunc);
	}
}
//module.exports = {
exports.default = new LibMuseJS();