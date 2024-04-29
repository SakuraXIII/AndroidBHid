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
object BHidUtils {
    var SelectedDeviceMac = ""
    var _connected = false
    var IsRegisted = false

    var mBluetoothAdapter: BluetoothAdapter =
        (AppUtils.getContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    var bluetoothProfile: BluetoothProfile? = null
    var BtDevice: BluetoothDevice? = null // 目标设备
    var HidDevice: BluetoothHidDevice? = null  // 蓝牙HID主机（本机）
    private var listener: HidServiceEventListener? = null


    fun RegistApp(listener: HidServiceEventListener) {
        try {
            // mProfileServiceListener 中 register
            if (!IsRegisted)
                mBluetoothAdapter.getProfileProxy(AppUtils.getContext(), mProfileServiceListener, BluetoothProfile.HID_DEVICE)
            BHidUtils.listener = listener

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
        BHidUtils._connected = _connected
    }

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
        val ret = HidDevice!!.connect(BtDevice)
        HidConfig.BtDevice = BtDevice
        HidConfig.HidDevice = HidDevice
        return ret
    }


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
//							RegistApp()
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
                listener?.HidServiceConnected()
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
//                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onDisConnected))
            } else if (state == BluetoothProfile.STATE_CONNECTED) {
                IsConnected(true)
//                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onConnected))
            } else if (state == BluetoothProfile.STATE_CONNECTING) {
//                EventBus.getDefault().post(HidEvent(HidEvent.tcpType.onConnecting))
            }
        }
    }

    interface HidServiceEventListener {
        fun HidServiceConnected()
    }
}