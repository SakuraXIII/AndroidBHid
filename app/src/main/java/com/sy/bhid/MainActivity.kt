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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		AppUtils.init(this)
		ScreenUtils.setFullScreen(this)
		setContent {
			BHidkeyboardTheme {
				Surface(Modifier.fillMaxSize(1f)) {
					Main(pairedDevices) {
						if (it == BDeviceUtils.BtDevice) {
							this.sendKey(arrayOf("ESC", "2", "0", "2", "3", "1", "0", "1", "5"))
						} else {
							val isRegistered = BDeviceUtils.connect(it.address)
							if (!isRegistered) {
								ToastUtils.showShortSafe("未成功注册为 HID 设备")
							}
						}
					}
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
		// BHidUtils.connect(BHidUtils.SelectedDeviceMac)
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

	private fun sendKey(msg: Array<String>) {
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

