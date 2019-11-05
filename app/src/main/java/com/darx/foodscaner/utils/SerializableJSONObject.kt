package com.darx.foodscaner.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


class SerializableJSONObject(jsonObject: JSONObject) : Serializable {
    @Transient
    var jsonObject: JSONObject? = null
        private set

    init {
        this.jsonObject = jsonObject
    }

    fun get(): JSONObject? {
        return this.jsonObject
    }

    @Throws(IOException::class)
    private fun writeObject(oos: ObjectOutputStream) {
        oos.defaultWriteObject()
        oos.writeObject(jsonObject!!.toString())
    }

    @Throws(ClassNotFoundException::class, IOException::class, JSONException::class)
    private fun readObject(ois: ObjectInputStream) {
        ois.defaultReadObject()
        jsonObject = JSONObject(ois.readObject() as String)
    }
}