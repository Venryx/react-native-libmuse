package v.LibMuse

import com.choosemuse.libmuse.Accelerometer
import com.choosemuse.libmuse.Eeg
import com.choosemuse.libmuse.MuseDataPacket
import com.choosemuse.libmuse.MuseDataPacketType
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

class VMuseDataPacket(var basePacket: MuseDataPacket) {
    abstract class Listener {
        abstract fun OnReceivePacket(var1: VMuseDataPacket?): Boolean
    }

    var baseType: MuseDataPacketType
    val type: String? get() {
        return when (baseType) {
            MuseDataPacketType.EEG -> "eeg"
            MuseDataPacketType.ACCELEROMETER -> "accel"
            else -> null
        }
    }

    lateinit var eegValues: DoubleArray
    lateinit var accelValues: DoubleArray
    fun LoadEEGValues() {
        eegValues = DoubleArray(4)
        eegValues!![0] = basePacket.getEegChannelValue(Eeg.EEG1)
        eegValues!![1] = basePacket.getEegChannelValue(Eeg.EEG2)
        eegValues!![2] = basePacket.getEegChannelValue(Eeg.EEG3)
        eegValues!![3] = basePacket.getEegChannelValue(Eeg.EEG4)
        /*eegValues[4] = basePacket.getEegChannelValue(Eeg.AUX_LEFT);
		eegValues[5] = basePacket.getEegChannelValue(Eeg.AUX_RIGHT);*/
    }

    fun LoadAccelValues() {
        accelValues = DoubleArray(3)
        accelValues[0] = basePacket.getAccelerometerValue(Accelerometer.X)
        accelValues[1] = basePacket.getAccelerometerValue(Accelerometer.Y)
        accelValues[2] = basePacket.getAccelerometerValue(Accelerometer.Z)
    }

    fun ToMap(): WritableMap {
        val map = Arguments.createMap()
        // maybe temp; don't send type
        //map.putString("type", Type());
        if (eegValues != null) {
            //map.putArray("eegValues", ToWritableArray(eegValues));
            // maybe temp; only send channels 1 and 2
            val eegValuesArray = Arguments.createArray()
            eegValuesArray.pushDouble(EnsureNormalDouble(eegValues!![1], fakeNaN))
            eegValuesArray.pushDouble(EnsureNormalDouble(eegValues!![2], fakeNaN))
            map.putArray("eegValues", eegValuesArray)
        }
        // maybe temp; don't send accel-values
        /*if (accelValues != null)
			map.putArray("accelValues", ToWritableArray(accelValues));*/return map
    }

    companion object {
        fun EnsureNormalDouble(value: Double, fallback: Double): Double {
            return if (java.lang.Double.isNaN(value) || java.lang.Double.isInfinite(value)) fallback else value
        }

        const val fakeNaN = -1000000000.0
        fun ToWritableArray(array: DoubleArray): WritableArray {
            val result = Arguments.createArray()
            for (`val` in array) {
                if (java.lang.Double.isNaN(`val`)) result.pushDouble(fakeNaN) else result.pushDouble(`val`)
            }
            return result
        }
    }

    init {
        baseType = basePacket.packetType()
    }
}