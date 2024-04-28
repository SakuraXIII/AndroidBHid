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
object BHidUtils {
    var SelectedDeviceMac = ""
    var _connected = false
    var IsRegisted = false

    var mBluetoothAdapter: BluetoothAdapter =
        (AppUtils.getContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    var bluetoothProfile: BluetoothProfile? = null
    var BtDevice: BluetoothDevice? = null
    var HidDevice: BluetoothHidDevice? = null


    private fun RegistApp() {
        try {
            // mProfileServiceListener 中 register
            if (!IsRegisted)
                mBluetoothAdapter.getProfileProxy(AppUtils.getContext(), mProfileServiceListener, BluetoothProfile.HID_DEVICE)

        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtils.showShort("当前系统不支持蓝牙遥控!")
        }
    }

    @SuppressLint("MissingPermission")
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
        BHidUtils._connected = _connected
    }

    @SuppressLint("MissingPermission")
    fun connect(deviceAddress: String?): Boolean {
        if (TextUtils.isEmpty(deviceAddress)) {
            ToastUtils.showShort("获取mac地址失败")
            return false
        }
        if (BtDevice == null) {
            BtDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress)
        }
        val ret = HidDevice!!.connect(BtDevice)
        HidConfig.BtDevice = BtDevice
        HidConfig.HidDevice = HidDevice
        return ret
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        val ret = HidDevice!!.connect(device)
        HidConfig.BtDevice = device
        HidConfig.HidDevice = HidDevice
        return ret
    }

    @SuppressLint("MissingPermission")
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
                            RegistApp()
                            Utils.DelayTask({ context.runOnUiThread { connect(SelectedDeviceMac) } }, 500, true)
                        }
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    private var mProfileServiceListener: BluetoothProfile.ServiceListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceDisconnected(profile: Int) {}

        @SuppressLint("NewApi", "MissingPermission")
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            bluetoothProfile = proxy
            if (profile == BluetoothProfile.HID_DEVICE) {
                HidDevice = proxy as BluetoothHidDevice
                HidConfig.HidDevice = HidDevice
                val sdp = BluetoothHidDeviceAppSdpSettings(
                    HidConfig.NAME,
                    HidConfig.DESCRIPTION,
                    HidConfig.PROVIDER,
                    BluetoothHidDevice.SUBCLASS1_COMBO,
                    HidConfig.DESCRIPTOR
                )
                HidDevice!!.registerApp(sdp, null, null, Executors.newCachedThreadPool(), mCallback)
            }
        }
    }

    val mCallback: BluetoothHidDevice.Callback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice, registered: Boolean) {
            IsRegisted = registered
        }

        override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
            if (state == BluetoothProfile.STATE_DISCONNECTED) {
                IsConnected(false)
                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onDisConnected))
            } else if (state == BluetoothProfile.STATE_CONNECTED) {
                IsConnected(true)
                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onConnected))
            } else if (state == BluetoothProfile.STATE_CONNECTING) {
                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onConnecting))
            }
        }
    }
}