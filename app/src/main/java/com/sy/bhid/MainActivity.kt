package com.sy.bhid

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import com.sy.bhid.ui.theme.BHidkeyboardTheme
import com.sy.bhid.utils.AppUtils
import com.sy.bhid.utils.BDeviceUtils
import com.sy.bhid.utils.HidUtils
import com.sy.bhid.utils.ScreenUtils
import com.sy.bhid.utils.ToastUtils
import com.sy.bhid.utils.Utils


class MainActivity : ComponentActivity(), BDeviceUtils.HidEventListener {
    private var pairedDevices by mutableStateOf<List<BluetoothDevice>>(listOf())
    private var connectedDevice by mutableStateOf<BluetoothDevice?>(null)
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.init(this)
        ScreenUtils.setFullScreen(this)
        sharedPreferences = this.getSharedPreferences("pwd", MODE_PRIVATE)
        setContent {
            BHidkeyboardTheme {
                Surface(Modifier.fillMaxSize(1f)) {
                    Main(pairedDevices, click = {
                        // 如果未连接则连接设备，连接则发送密码
                        if (it == BDeviceUtils.BtDevice) {
                            val pwd = (this.getBDevicePwd(it.address) as String).split("")
                            this.sendKey(listOf("ESC"))
                            this.sendKey(pwd)
                        } else {
                            val isRegistered = BDeviceUtils.connect(it.address)
                            if (!isRegistered) {
                                ToastUtils.showShortSafe("未成功注册为 HID 设备")
                            }
                        }
                    }, savePwd = { address, pwd -> this.setBDevicePwd(address, pwd.trim()) })
                }
            }
        }
        Utils.showLog(isSupportBluetoothHid().toString())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun init() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.all { permission -> permission.value }) {
                BDeviceUtils.RegistApp(this)
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        // 程序进入后台时，HID 会自动取消注册，所以无论是初始化还是从后台进入前台，都要重新注册
        init()
        // 用于程序从后台进入前台时的触发
        BDeviceUtils.reConnect(this)
    }

    override fun onStop() {
        super.onStop()
        // 同步连接设备对象
        connectedDevice = BDeviceUtils.BtDevice
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭 handler，线程执行器，取消注册HID
        HidUtils.exit()
    }


    /**
     * HID 服务连接
     */
    override fun ServiceConnected() {
        super.ServiceConnected()
        pairedDevices = (BDeviceUtils.getPairedDevices()?.toList() ?: listOf())
    }

    /**
     * HID 连接到蓝牙设备
     */
    override fun DeviceConnected(device: BluetoothDevice) {
        super.DeviceConnected(device)
        connectedDevice = device
    }

    /**
     * HID 连接蓝牙设备连接中
     */
    override fun DeviceConnecting(device: BluetoothDevice) {
        super.DeviceConnecting(device)
    }

    /**
     * HID 断开连接蓝牙设备
     */
    override fun DeviceDisconnected(device: BluetoothDevice) {
        super.DeviceDisconnected(device)
        connectedDevice = null
    }

    private fun sendKey(msg: List<String>) {
        msg.forEach {
            HidUtils.keyDown(it)
            HidUtils.stopKey()
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

    private fun getBDevicePwd(key: String?): Any? {
        if (key == null) {
            return sharedPreferences.all
        }
        return sharedPreferences.getString(key, "")
    }

    private fun setBDevicePwd(key: String, pwd: String) {
        sharedPreferences.edit {
            this.putString(key, pwd)
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun Main(
        pairedDevices: List<BluetoothDevice>,
        click: (item: BluetoothDevice) -> Unit,
        savePwd: (address: String, pwd: String) -> Unit
    ) {
        var deviceAddress by remember { mutableStateOf("") }
        val lazylistState = rememberLazyListState()
        Box {
            Box {
                LazyColumn(verticalArrangement = Arrangement.Center, contentPadding = PaddingValues(1.dp), state = lazylistState) {
                    items(pairedDevices) { device ->
                        Box(modifier = Modifier
                            .background(Color.Blue, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        Utils.showLog("点击事件")
                                        click(device)
                                    }, onDoubleTap = {
                                        Utils.showLog("双击事件")
                                    }, onPress = {
                                        Utils.showLog("触摸事件")
                                    }, onLongPress = {
                                        Utils.showLog("长按事件")
                                        deviceAddress = device.address
                                    }
                                )
                            }) {
                            Column {
                                if (device == connectedDevice) {
                                    Text(text = "已连接")
                                }
                                Text(text = device.name)
                                Text(text = device.address)
                            }
                        }
                    }
                }
            }
            if (deviceAddress != "")
                ShowInput(onDismiss = {
                    Utils.showLog("dismiss")
                    deviceAddress = ""
                }, onConfirm = {
                    Utils.showLog("confirm")
                    savePwd(deviceAddress, it)
                })
        }

    }


    @Composable
    fun ShowInput(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
        var text by remember { mutableStateOf("") }

        AlertDialog(
            icon = {},
            title = {},
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(text)
                    onDismiss()
                }) {
                    Text("保存")
                }
            },
            text = {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Password") }
                )
            }
        )
    }
}