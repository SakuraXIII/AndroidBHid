package com.sy.bhid

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BluetoothListenerReceiver : BroadcastReceiver() {
	override fun onReceive(context: Context, intent: Intent) {
		when (intent.action) {
			BluetoothAdapter.ACTION_STATE_CHANGED -> {
				val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
				when (blueState) {
					BluetoothAdapter.STATE_TURNING_ON -> {
						Log.e("BluetoothListener", "onReceive---------蓝牙正在打开中")
						if (mBluetoothStateListener != null) {
							mBluetoothStateListener!!.stateTurningOn()
						}
					}

					BluetoothAdapter.STATE_ON -> {
						Log.e("BluetoothListener", "onReceive---------蓝牙已经打开")
						if (mBluetoothStateListener != null) {
							mBluetoothStateListener!!.stateOn()
						}
					}

					BluetoothAdapter.STATE_TURNING_OFF -> {
						Log.e("BluetoothListener", "onReceive---------蓝牙正在关闭中")
						if (mBluetoothStateListener != null) {
							mBluetoothStateListener!!.stateTurningOff()
						}
					}

					BluetoothAdapter.STATE_OFF -> {
						Log.e("BluetoothListener", "onReceive---------蓝牙已经关闭")
						if (mBluetoothStateListener != null) {
							mBluetoothStateListener!!.stateOff()
						}
					}
				}
			}
		}
	}

	private var mBluetoothStateListener: BluetoothStateListener? = null
	fun setmBluetoothStateListener(mBluetoothStateListener: BluetoothStateListener?) {
		this.mBluetoothStateListener = mBluetoothStateListener
	}

	interface BluetoothStateListener {
		fun stateTurningOn()
		fun stateOn()
		fun stateTurningOff()
		fun stateOff()
	}
}