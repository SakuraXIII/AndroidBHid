package com.sy.bhid

import android.Manifest
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
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sy.bhid.bk.BluetoothKeyboard
import com.sy.bhid.ui.theme.BHidkeyboardTheme
import com.sy.bhid.utils.AppUtils
import com.sy.bhid.utils.ScreenUtils
import com.sy.bhid.utils.ToastUtils
import com.sy.bhid.utils.Utils


class MainActivity : ComponentActivity() {
	private lateinit var bhid: BluetoothKeyboard

	@RequiresApi(Build.VERSION_CODES.S)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		AppUtils.init(this)
		ScreenUtils.setFullScreen(this)
		setContent {
			BHidkeyboardTheme {
				Surface() {
					Main(name = bhid.hostname, mac = bhid.hostMac) {
						this.sendKey(arrayOf("ESC", "2", "0", "2", "2", "0", "9", "1", "0"))
					}
				}
			}
		}
		registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
			if (it.all { permission -> permission.value }) {
				bhid = BluetoothKeyboard(this)
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
		Utils.showLog(isSupportBluetoothHid().toString())
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