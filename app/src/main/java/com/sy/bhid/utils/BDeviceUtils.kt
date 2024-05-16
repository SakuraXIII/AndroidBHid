package com.sy.bhid.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.text.TextUtils
import java.util.concurrent.Executors

/**
 * 蓝牙配对，连接等状态管理
 */
@SuppressLint("MissingPermission")
object BDeviceUtils {
	var SelectedDeviceMac = ""
	var _connected = false
	var IsRegisted = false

	var mBluetoothAdapter: BluetoothAdapter =
		(AppUtils.getContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
	var bluetoothProfile: BluetoothProfile? = null
	var BtDevice: BluetoothDevice? = null // 目标设备
	var HidDevice: BluetoothHidDevice? = null  // 蓝牙HID主机（本机）
	private var listener: HidEventListener? = null


	fun RegistApp(listener: HidEventListener) {
		try {
			// mProfileServiceListener 中 register
			if (!IsRegisted)
				mBluetoothAdapter.getProfileProxy(AppUtils.getContext(), mProfileServiceListener, BluetoothProfile.HID_DEVICE)
			BDeviceUtils.listener = listener

		} catch (e: Exception) {
			e.printStackTrace()
			ToastUtils.showShortSafe("当前系统不支持蓝牙遥控!")
		}
	}

	fun getPairedDevices(): MutableSet<BluetoothDevice>? {
		return mBluetoothAdapter.bondedDevices
	}


	fun Pair(deviceAddress: String?): Boolean {
		if (BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
			try {
				if (BtDevice == null) {
					BtDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress)
				}
				when (BtDevice?.bondState) {
					BluetoothDevice.BOND_NONE -> {
						BtDevice!!.createBond()
						return false
					}

					BluetoothDevice.BOND_BONDED -> {
						return true
					}

					BluetoothDevice.BOND_BONDING -> {
						return false
					}
				}
			} catch (ex: Exception) {
				ex.printStackTrace()
			}
		}
		return false
	}

	fun IsConnected(): Boolean {
		return try {
			_connected
		} catch (e: Exception) {
			e.printStackTrace()
			false
		}
	}

	private fun IsConnected(_connected: Boolean) {
		BDeviceUtils._connected = _connected
	}

	/**
	 * 连接指定 MAC 设备，如果已有连接的设备，则断开当前连接
	 */
	fun connect(deviceAddress: String?): Boolean {
		if (TextUtils.isEmpty(deviceAddress)) {
			ToastUtils.showShortSafe("获取mac地址失败")
			return false
		}
		SelectedDeviceMac = deviceAddress!!
		if (BtDevice == null) {
			BtDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress)
		} else {
			HidDevice!!.disconnect(BtDevice)
		}
		val isRegistered = HidDevice!!.connect(BtDevice)
		HidUtils.BtDevice = BtDevice
		HidUtils.HidDevice = HidDevice
		return isRegistered
	}

	/**
	 * 重新连接选中过的设备，如果没有连接过的设备，则直接返回。适合用于从后台返回前台的重新连接
	 */
	fun reConnect(context: Activity) {
		if (TextUtils.isEmpty(SelectedDeviceMac)) return
		try {
			if (HidDevice != null) {
				if (BtDevice == null) {
					BtDevice = mBluetoothAdapter.getRemoteDevice(SelectedDeviceMac)
				}
				val state: Int = HidDevice!!.getConnectionState(BtDevice)
				if (state == BluetoothProfile.STATE_DISCONNECTED) {
					if (TextUtils.isEmpty(SelectedDeviceMac)) {
						return
					} else {
						if (Pair(SelectedDeviceMac)) {
							Utils.DelayTask({
								context.runOnUiThread {
									val result = connect(SelectedDeviceMac)
									Utils.showLog(result.toString())
								}
							}, 500, true)
						}
					}
				}
			}
		} catch (_: Exception) {
		}
	}

	private var mProfileServiceListener: BluetoothProfile.ServiceListener = object : BluetoothProfile.ServiceListener {
		override fun onServiceDisconnected(profile: Int) {
			listener?.ServiceDisConnected()
		}

		override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
			bluetoothProfile = proxy
			if (profile == BluetoothProfile.HID_DEVICE) {
				HidDevice = proxy as BluetoothHidDevice
				HidUtils.HidDevice = HidDevice
				val sdp = BluetoothHidDeviceAppSdpSettings(
					HidUtils.NAME,
					HidUtils.DESCRIPTION,
					HidUtils.PROVIDER,
					BluetoothHidDevice.SUBCLASS1_COMBO,
					HidUtils.DESCRIPTOR
				)
				HidDevice!!.registerApp(sdp, null, null, Executors.newCachedThreadPool(), mCallback)
				listener?.ServiceConnected()
			}
		}
	}

	val mCallback: BluetoothHidDevice.Callback = object : BluetoothHidDevice.Callback() {
		override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
			IsRegisted = registered
		}

		override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
			when (state) {
				BluetoothProfile.STATE_DISCONNECTED -> {
					ToastUtils.showShortSafe("断开连接")
					Utils.showLog("DISCONNECTED!")
					IsConnected(false)
					BtDevice = null
					listener?.DeviceDisconnected(device)
				}

				BluetoothProfile.STATE_CONNECTED -> {
					Utils.showLog("CONNECTED!")
					IsConnected(true)
					ToastUtils.showShortSafe("连接成功")
					listener?.DeviceConnected(device)
				}

				BluetoothProfile.STATE_CONNECTING -> {
					Utils.showLog("CONNECTING...")
					ToastUtils.showShortSafe("连接中...")
					listener?.DeviceConnecting(device)
				}
			}
		}
	}

	interface HidEventListener {
		/**
		 * HID 服务连接
		 */
		fun ServiceConnected() {}

		/**
		 * HID 服务断开
		 */
		fun ServiceDisConnected() {}

		/**
		 * HID 连接到蓝牙设备
		 */

		fun DeviceConnected(device: BluetoothDevice) {}

		/**
		 * HID 连接蓝牙设备连接中
		 */
		fun DeviceConnecting(device: BluetoothDevice) {}

		/**
		 * HID 断开连接蓝牙设备
		 */
		fun DeviceDisconnected(device: BluetoothDevice) {}
	}
}