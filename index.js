/**
 * @providesModule react-native-libmuse
 */

var LibMuseLink = require("react-native").NativeModules.LibMuseLink;

module.exports = {
	Start: function () {
		LibMuseLink.Start();
	},
	Connect: function () {
		LibMuseLink.Connect();
	},
	Disconnect: function () {
		LibMuseLink.Disconnect();
	},
};