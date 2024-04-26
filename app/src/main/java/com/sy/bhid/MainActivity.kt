package com.sy.bhid

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


class MainActivity : ComponentActivity() {
    private lateinit var bhid: BluetoothKeyboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface() {
                    Main(name = bhid.hostname, mac = bhid.hostMac) {
                        this.sendKey(arrayOf("ESC", "2", "0", "2", "2", "0", "9", "1", "0"))
                    }
                }
            }
        }
        Utils.checkAndRequestPermissions(
            this, arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        )
        Utils.showLog(isSupportBluetoothHid().toString())
        bhid = BluetoothKeyboard(this)
    }


    private fun sendKey(msg: Array<String>) {
        msg.forEach {
            if (it.isNotEmpty()) {
                this.bhid.sendKey(it)
            }
        }
    }

    fun isSupportBluetoothHid(): Boolean {
        val intent = Intent("android.bluetooth.IBluetoothHidDevice")
        val results: List<ResolveInfo> = this.packageManager.queryIntentServices(intent, 0) ?: return false
        var comp: ComponentName? = null
        for (i in results.indices) {
            val ri = results[i]
            if (ri.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                continue
            }
            val foundComp = ComponentName(
                ri.serviceInfo.applicationInfo.packageName,
                ri.serviceInfo.name
            )
            check(comp == null) {
                ("Multiple system services handle " + this
                        + ": " + comp + ", " + foundComp)
            }
            comp = foundComp
        }
        return comp != null
    }

}

@Composable
fun Main(name: String, mac: String, click: () -> Unit) {
    Text(text = name)
    Text(text = mac)
    Button(onClick = click) {
        Text(text = "Send")
    }
}