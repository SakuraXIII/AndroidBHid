package com.sy.bhid

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sy.bhid.bk.BluetoothKeyboard
import com.sy.bhid.ui.theme.BHidkeyboardTheme
import com.sy.bhid.utils.AppUtils
import com.sy.bhid.utils.BHidUtils
import com.sy.bhid.utils.ScreenUtils
import com.sy.bhid.utils.ToastUtils
import com.sy.bhid.utils.Utils
import com.sy.bhid.utils.fillMaxSize


class MainActivity : ComponentActivity() {
    private var pairedDevices by mutableStateOf<List<BluetoothDevice>>(listOf())
    private var connectedDevice by mutableStateOf<BluetoothDevice?>(null)

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.init(this)
        ScreenUtils.setFullScreen(this)
        setContent {
            BHidkeyboardTheme {
                Surface(Modifier.fillMaxSize(1f)) {
                    Main(pairedDevices) {
                        val isConnected = BHidUtils.connect(it.address)
                        ToastUtils.showShortSafe(if (isConnected) "连接成功" else "连接失败")
//                        this.sendKey(arrayOf("ESC", "2", "0", "2", "2", "0", "9", "1", "0"))
                    }
                }
            }
        }
        init()
        Utils.showLog(isSupportBluetoothHid().toString())

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun init() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.all { permission -> permission.value }) {
                BHidUtils.RegistApp(object : BHidUtils.HidServiceEventListener {
                    override fun HidServiceConnected() {
                        pairedDevices = (BHidUtils.getPairedDevices()?.toList() ?: listOf())
//                        BHidUtils.connect(BHidUtils.SelectedDeviceMac)
                    }
                })
            } else {
                ToastUtils.showLongSafe("请授予所有权限")
            }
        }.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
            )
        )
    }

    private fun sendKey(msg: Array<String>) {
        msg.forEach {
            if (it.isNotEmpty()) {
//                this.bhid?.sendKey(it)
            }
        }
    }

    private fun isSupportBluetoothHid(): Boolean {
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

    @SuppressLint("MissingPermission")
    @Composable
    fun Main(pairedDevices: List<BluetoothDevice>, click: (item: BluetoothDevice) -> Unit) {
        val lazylistState = rememberLazyListState()
        LazyColumn(verticalArrangement = Arrangement.Center, contentPadding = PaddingValues(1.dp), state = lazylistState) {
            items(pairedDevices) {
                Button(onClick = { click(it) }) {
                    Column {
                        if (it == connectedDevice) {
                            Text(text = "已连接")
                        }
                        Text(text = it.name)
                        Text(text = it.address)
                    }
                }
            }
        }

    }
}

