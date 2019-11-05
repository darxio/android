package com.darx.foodscaner.utils

import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable


class SerializableJSONArray(jsonArray: JSONArray) : Serializable {
    @Transient
    var jsonArray: JSONArray? = null
        private set

    init {
        this.jsonArray = jsonArray
    }

    fun get(): JSONArray? {
        return this.jsonArray
    }

    @Throws(IOException::class)
    private fun writeObject(oos: ObjectOutputStream) {
        oos.defaultWriteObject()
        oos.writeObject(jsonArray!!.toString())
    }

    @Throws(ClassNotFoundException::class, IOException::class, JSONException::class)
    private fun readObject(ois: ObjectInputStream) {
        ois.defaultReadObject()
        jsonArray = JSONArray(ois.readObject() as String)
    }
}