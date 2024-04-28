package com.sy.bhid

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


abstract class BluetoothListenerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                when (blueState) {
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Log.e("BluetoothListener", "onReceive---------蓝牙正在打开中")
                        mBluetoothStateListener.stateTurningOn()
                    }

                    BluetoothAdapter.STATE_ON -> {
                        Log.e("BluetoothListener", "onReceive---------蓝牙已经打开")
                        mBluetoothStateListener.stateOn()
                    }

                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Log.e("BluetoothListener", "onReceive---------蓝牙正在关闭中")
                        mBluetoothStateListener.stateTurningOff()
                    }

                    BluetoothAdapter.STATE_OFF -> {
                        Log.e("BluetoothListener", "onReceive---------蓝牙已经关闭")
                        mBluetoothStateListener.stateOff()
                    }
                }
            }
        }
    }

    private lateinit var mBluetoothStateListener: BluetoothStateListener
    fun setmBluetoothStateListener(mBluetoothStateListener: BluetoothStateListener) {
        this.mBluetoothStateListener = mBluetoothStateListener
    }

    interface BluetoothStateListener {
        fun stateTurningOn()
        fun stateOn()
        fun stateTurningOff()
        fun stateOff()
    }
}