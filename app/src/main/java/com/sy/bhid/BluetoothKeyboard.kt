package com.sy.bhid

import android.bluetooth.BluetoothDevice
import android.content.Context
import java.util.Locale

class BluetoothKeyboard(context: Context) {
    private val bluetoothClient: BluetoothClient? = BluetoothClient.bindContext(context)
    var hostname: String = ""
    var hostMac: String = ""

    init {
        bluetoothClient?.setListener(object : BluetoothClient.Listener {
            override fun onConnected(name: String, mac: String) {
                hostname = name
                hostMac = mac
            }

            override fun onDisConnected() {
                hostname = ""
                hostMac = ""
                active()
            }
        })
    }

    fun sendKey(key: String) {
        var b1: Byte = 0
        if (key.length <= 1) {
            val keyChar = key[0]
            if (keyChar.code in 65..90) {
                b1 = 2
            }
        }
        if (SHITBYTE.containsKey(key)) {
            b1 = 2
        }
        bluetoothClient?.sendData(
            8, byteArrayOf(
                b1, 0,
                KEY2BYTE[key.uppercase(Locale.getDefault())]!!,
                0, 0, 0, 0, 0
            )
        )
        bluetoothClient?.sendData(8, byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0))
    }

    fun active() {
        bluetoothClient?.active()
    }

    fun stop() {
        bluetoothClient?.stop()
    }

    companion object {
        var KEY2BYTE: MutableMap<String, Byte> = HashMap()
        var SHITBYTE: MutableMap<String, Boolean> = HashMap()

        init {
            KEY2BYTE["A"] = 4.toByte()
            KEY2BYTE["B"] = 5.toByte()
            KEY2BYTE["C"] = 6.toByte()
            KEY2BYTE["D"] = 7.toByte()
            KEY2BYTE["E"] = 8.toByte()
            KEY2BYTE["F"] = 9.toByte()
            KEY2BYTE["G"] = 0.toByte()
            KEY2BYTE["H"] = 11.toByte()
            KEY2BYTE["I"] = 12.toByte()
            KEY2BYTE["J"] = 13.toByte()
            KEY2BYTE["K"] = 14.toByte()
            KEY2BYTE["L"] = 15.toByte()
            KEY2BYTE["M"] = 16.toByte()
            KEY2BYTE["N"] = 17.toByte()
            KEY2BYTE["O"] = 18.toByte()
            KEY2BYTE["P"] = 19.toByte()
            KEY2BYTE["Q"] = 20.toByte()
            KEY2BYTE["R"] = 21.toByte()
            KEY2BYTE["S"] = 22.toByte()
            KEY2BYTE["T"] = 23.toByte()
            KEY2BYTE["U"] = 24.toByte()
            KEY2BYTE["V"] = 25.toByte()
            KEY2BYTE["W"] = 26.toByte()
            KEY2BYTE["X"] = 27.toByte()
            KEY2BYTE["Y"] = 28.toByte()
            KEY2BYTE["Z"] = 29.toByte()
            KEY2BYTE["1"] = 30.toByte()
            KEY2BYTE["2"] = 31.toByte()
            KEY2BYTE["3"] = 32.toByte()
            KEY2BYTE["4"] = 33.toByte()
            KEY2BYTE["5"] = 34.toByte()
            KEY2BYTE["6"] = 35.toByte()
            KEY2BYTE["7"] = 36.toByte()
            KEY2BYTE["8"] = 37.toByte()
            KEY2BYTE["9"] = 38.toByte()
            KEY2BYTE["0"] = 39.toByte()
            KEY2BYTE["ENTER"] = 40.toByte()
            KEY2BYTE["ESC"] = 41.toByte()
            KEY2BYTE["BACK_SPACE"] = 42.toByte()
            KEY2BYTE["TAB"] = 43.toByte()
            KEY2BYTE["SPACE"] = 44.toByte()
            KEY2BYTE["-"] = 45.toByte()
            KEY2BYTE["="] = 46.toByte()
            KEY2BYTE["["] = 47.toByte()
            KEY2BYTE["]"] = 48.toByte()
            KEY2BYTE["\\"] = 49.toByte()
            KEY2BYTE[";"] = 51.toByte()
            KEY2BYTE["'"] = 52.toByte()
            KEY2BYTE["`"] = 53.toByte()
            KEY2BYTE[","] = 54.toByte()
            KEY2BYTE["."] = 55.toByte()
            KEY2BYTE["/"] = 56.toByte()
            KEY2BYTE["SCROLL_LOCK"] = 71.toByte()
            KEY2BYTE["INSERT "] = 73.toByte()
            KEY2BYTE["HOME "] = 74.toByte()
            KEY2BYTE["PAGE_UP  "] = 75.toByte()
            KEY2BYTE["DELETE "] = 76.toByte()
            KEY2BYTE["END "] = 77.toByte()
            KEY2BYTE["PAGE_DOWN "] = 78.toByte()
            KEY2BYTE["DPAD_RIGHT "] = 79.toByte()
            KEY2BYTE["KEYCODE_DPAD_LEFT "] = 80.toByte()
            KEY2BYTE["KEYCODE_DPAD_DOWN "] = 81.toByte()
            KEY2BYTE["KEYCODE_DPAD_UP "] = 82.toByte()
            KEY2BYTE["NUM_LOCK "] = 83.toByte()
            KEY2BYTE["!"] = 30.toByte()
            SHITBYTE["!"] = true
            KEY2BYTE["@"] = 31.toByte()
            SHITBYTE["@"] = true
            KEY2BYTE["#"] = 32.toByte()
            SHITBYTE["#"] = true
            KEY2BYTE["$"] = 33.toByte()
            SHITBYTE["$"] = true
            KEY2BYTE["%"] = 34.toByte()
            SHITBYTE["%"] = true
            KEY2BYTE["^"] = 35.toByte()
            SHITBYTE["^"] = true
            KEY2BYTE["&"] = 36.toByte()
            SHITBYTE["&"] = true
            KEY2BYTE["*"] = 37.toByte()
            SHITBYTE["*"] = true
            KEY2BYTE["("] = 38.toByte()
            SHITBYTE["("] = true
            KEY2BYTE[")"] = 39.toByte()
            SHITBYTE[")"] = true
            KEY2BYTE["_"] = 45.toByte()
            SHITBYTE["_"] = true
            KEY2BYTE["+"] = 46.toByte()
            SHITBYTE["+"] = true
            KEY2BYTE["{"] = 47.toByte()
            SHITBYTE["{"] = true
            KEY2BYTE["}"] = 48.toByte()
            SHITBYTE["}"] = true
            KEY2BYTE["|"] = 49.toByte()
            SHITBYTE["|"] = true
            KEY2BYTE[":"] = 51.toByte()
            SHITBYTE[":"] = true
            KEY2BYTE["\""] = 52.toByte()
            SHITBYTE["\""] = true
            KEY2BYTE["<"] = 54.toByte()
            SHITBYTE["<"] = true
            KEY2BYTE[">"] = 55.toByte()
            SHITBYTE[">"] = true
            KEY2BYTE["?"] = 56.toByte()
            SHITBYTE["?"] = true
        }
    }
}