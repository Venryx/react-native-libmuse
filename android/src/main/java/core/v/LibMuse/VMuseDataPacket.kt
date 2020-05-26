package v.LibMuse

import com.choosemuse.libmuse.Accelerometer
import com.choosemuse.libmuse.Eeg
import com.choosemuse.libmuse.MuseDataPacket
import com.choosemuse.libmuse.MuseDataPacketType

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

    init {
        baseType = basePacket.packetType()
    }
}