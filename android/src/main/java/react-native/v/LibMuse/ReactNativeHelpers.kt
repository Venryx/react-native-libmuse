package v.LibMuse

import com.facebook.react.bridge.*
import java.util.*

object RNHelpers {
    fun EnsureNormalDouble(value: Double, fallback: Double): Double {
        return if (java.lang.Double.isNaN(value) || java.lang.Double.isInfinite(value)) fallback else value
    }

    fun List_ReadableMaps(array: ReadableArray): List<ReadableMap> {
        val result: MutableList<ReadableMap> = ArrayList()
        for (i in 0 until array.size()) result.add(array.getMap(i))
        return result
    }

    fun ToObjectArray(`val`: Any?): Array<Any?> {
        val length = java.lang.reflect.Array.getLength(`val`)
        val outputArray = arrayOfNulls<Any>(length)
        for (i in 0 until length) outputArray[i] = java.lang.reflect.Array.get(`val`, i)
        return outputArray
    }

    const val fakeNaN = -1000000000.0
    fun ToWritableArray(array: DoubleArray): WritableArray {
        val result = Arguments.createArray()
        for (`val` in array) {
            if (java.lang.Double.isNaN(`val`)) result.pushDouble(fakeNaN) else result.pushDouble(`val`)
        }
        return result
    }

    fun <T> ToWritableArray(array: Array<T>): WritableArray {
        val result = Arguments.createArray()
        for (item in array) WritableArray_Add(result, item)
        return result
    }

    fun <T : Any?> ToWritableArray(array: List<T>): WritableArray {
        val result = Arguments.createArray()
        for (item in array) WritableArray_Add(result, item)
        return result
    }

    //fun <K : String?, V : Any?> ToWritableMap(map: HashMap<K, V>): WritableMap {
    fun <K : Any?, V : Any?> ToWritableMap(map: HashMap<K, V>): WritableMap {
        val result = Arguments.createMap()
        for ((key, value) in map) WritableMap_Add(result, key as String, value)
        return result
    }

    fun WritableArray_Add(array: WritableArray, item: Any?) {
        // for types that are invalid, but can easily be cast to a valid one, do so
        var item = item
        if (item is Float) item = item.toDouble()
        if (item == null) array.pushNull() else if (item is Boolean) array.pushBoolean((item as Boolean?)!!) else if (item is Int) array.pushInt((item as Int?)!!) else if (item is Double) array.pushDouble((item as Double?)!!) else if (item is String) array.pushString(item as String?) else if (item is WritableArray) array.pushArray(item as WritableArray?) else {
            //Assert(arg instanceof WritableMap, "Event args must be one of: WritableArray, Boolean")
            if (item !is WritableMap) throw RuntimeException("Event args must be one of: Boolean, Integer, Double, String, WritableArray, WritableMap (not " + item.javaClass.simpleName + ")")
            array.pushMap(item as WritableMap?)
        }
    }

    fun WritableMap_Add(map: WritableMap, key: String?, obj: Any?) {
        if (obj == null) map.putNull(key) else if (obj is Boolean) map.putBoolean(key, (obj as Boolean?)!!) else if (obj is Int) map.putInt(key, (obj as Int?)!!) else if (obj is Double) map.putDouble(key, (obj as Double?)!!) else if (obj is String) map.putString(key, obj as String?) else if (obj is WritableArray) map.putArray(key, obj as WritableArray?) else {
            //Assert(arg instanceof WritableMap, "Event args must be one of: WritableArray, Boolean")
            if (obj !is WritableMap) throw RuntimeException("Event args must be one of: Boolean, Integer, Double, String, WritableArray, WritableMap (not " + obj.javaClass.simpleName + ")")
            map.putMap(key, obj as WritableMap?)
        }
    }

    fun VMuseDataPacket_ToMap(packet: VMuseDataPacket): WritableMap {
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
}