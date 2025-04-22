package com.sy.bhid.bk

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppQosSettings
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.sy.bhid.utils.HidUtils
import com.sy.bhid.utils.Utils
import java.util.concurrent.Executors


class BluetoothClient private constructor(private val context: Context) :
	BluetoothHidDevice.Callback() {

	private var btHid: BluetoothHidDevice? = null
	private val bluetoothDevice: BluetoothDevice? = null
	private var hostDevice: BluetoothDevice? = null
	private var mpluggedDevice: BluetoothDevice? = null
	private val btAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
	private val serviceListener: ServiceListener = ServiceListener()
	private var listener: Listener? = null
	private val sdpRecord = BluetoothHidDeviceAppSdpSettings(
        HidUtils.NAME,
        HidUtils.DESCRIPTION,
        HidUtils.PROVIDER,
		BluetoothHidDevice.SUBCLASS1_COMBO,
        HidUtils.DESCRIPTOR
	)
	private val qosOut = BluetoothHidDeviceAppQosSettings(
		BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
		800,
		9,
		0,
		11250,
		BluetoothHidDeviceAppQosSettings.MAX
	)

	interface Listener {
		fun onConnected(name: String, mac: String)
		fun onDisConnected()
	}

	init {
		init()
	}

	@SuppressLint("MissingPermission")
	private fun init() {
		if (btHid != null) {
			return
		}
		btAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HID_DEVICE)
	}

	companion object {
		@SuppressLint("StaticFieldLeak")
		private var instance: BluetoothClient? = null

		fun bindContext(context: Context): BluetoothClient? {
			if (instance != null) {
				Utils.showLog("BluetoothClient already bind a Context")
				return null
			}
			instance = BluetoothClient(context)
			return instance
		}
	}

	fun setListener(listener: Listener?) {
		this.listener = listener
	}

	@SuppressLint("MissingPermission")
	fun connect() {
		btHid?.connect(mpluggedDevice)
	}

	@SuppressLint("MissingPermission")
	fun sendData(id: Int, data: ByteArray?) {
		btHid?.sendReport(hostDevice, id, data)
	}

	@SuppressLint("MissingPermission")
	fun active() {
		val status = btHid?.registerApp(
			sdpRecord, null, qosOut,
			Executors.newCachedThreadPool(), this@BluetoothClient
		)
		Utils.showLog(status.toString())
	}

	@SuppressLint("MissingPermission")
	fun stop() {
		btHid?.unregisterApp()
	}

	fun destory() {
		btAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, btHid)
	}

	@SuppressLint("MissingPermission")
	private inner class ServiceListener : BluetoothProfile.ServiceListener {
		override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
			Utils.showLog("Connected to service")
			if (profile != BluetoothProfile.HID_DEVICE) {
				Utils.showLog("WTF:$profile")
				return
			}
			btHid = proxy as BluetoothHidDevice
			active()
		}

		override fun onServiceDisconnected(profile: Int) {
			Utils.showLog("Service disconnected!", level = Log.ERROR)
			if (profile == BluetoothProfile.HID_DEVICE) {
				btHid = null
			}
		}
	}

	@SuppressLint("MissingPermission")
	override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
		super.onConnectionStateChanged(device, state)
		Utils.showLog("onConnectionStateChanged:$device  state:$state", level = Log.WARN)
		when (state) {
			BluetoothProfile.STATE_CONNECTED -> {
				hostDevice = device
				listener?.onConnected(device.name, device.address)
				Utils.showLog("if connected")
			}

			else -> {
				hostDevice = null
				listener?.onDisConnected()
			}
		}
	}

	@SuppressLint("MissingPermission")
	override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
		super.onAppStatusChanged(pluggedDevice, registered)
		if (pluggedDevice != null) {
			Utils.showLog("onAppStatusChanged:" + pluggedDevice.getName() + " registered:" + registered, level = Log.DEBUG)
		}
		if (registered) {
			val states = intArrayOf(
				BluetoothProfile.STATE_DISCONNECTED,
				BluetoothProfile.STATE_CONNECTING,
				BluetoothProfile.STATE_CONNECTED,
				BluetoothProfile.STATE_DISCONNECTING
			)
			val pairedDevices = btHid?.getDevicesMatchingConnectionStates(states)
			Utils.showLog("paired devices: $pairedDevices")
			mpluggedDevice = pluggedDevice
			btHid?.apply {
				val remoteDevice = btAdapter.getRemoteDevice("38:7A:0E:A4:05:78")
				this.connect(remoteDevice)
//				if (this.getConnectionState(pluggedDevice) == BluetoothProfile.STATE_DISCONNECTED)
//					this.connect(pluggedDevice)
//				else {
//					val pairedDevice = pairedDevices?.get(0)
//					val pairedDState = this.getConnectionState(pairedDevice)
//					Utils.showLog("paired $pairedDState")
//					if (pairedDState == BluetoothProfile.STATE_DISCONNECTED) {
//						this.connect(pairedDevice)
//					}
//				}
			}
		}
	}
}