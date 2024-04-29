package com.sy.bhid

import android.R
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sy.bhid.ui.theme.BHidkeyboardTheme
import java.util.Arrays
import java.util.Collections


//class ScanActivity : ComponentActivity() {
//
//	private val mBluetoothAdapter: BluetoothAdapter? = null
//	private val mBluetoothDevice: BluetoothDevice? = null
//
//	private val mRecyclerview: RecyclerView? = null
//	private val mBleDeviceAdpter: BleDeviceAdpter? = null
//	private val datas: List<MBluetoothDevice>? = null
//	private val mLoadingDialog: LoadingDialog? = null
//	override fun onCreate(savedInstanceState: Bundle?) {
//		super.onCreate(savedInstanceState)
//		setContent {
//			BHidkeyboardTheme {
//				// A surface container using the 'background' color from the theme
//				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
//					Greeting("Android")
//				}
//			}
//		}
//	}
//}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//	Text(
//		text = "Hello $name!",
//		modifier = modifier
//	)
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//	BHidkeyboardTheme {
//		Greeting("Android")
//	}
//}


//class ScanActivity() : XActivity() {
//	private var mBluetoothAdapter: BluetoothAdapter? = null
//	private var mBluetoothDevice: BluetoothDevice? = null
//	private var mRecyclerview: RecyclerView? = null
//	private var mBleDeviceAdpter: BleDeviceAdpter? = null
//	private var datas: MutableList<MBluetoothDevice>? = null
//	private var mLoadingDialog: LoadingDialog? = null
//
//	//循环开启扫描功能
//	private val handler: Handler? = Handler()
//	private val runnable: Runnable? = Runnable { startDiscovery() }
//
//	//显示无法连接
//	private val handler2: Handler? = Handler()
//	private val runnable2: Runnable? = Runnable {
//		if (mLoadingDialog != null) {
//			mLoadingDialog.dismiss()
//		}
//		showDisConnectDialog()
//	}
//	val layoutId: Int
//		get() = R.layout.activity_scan
//	val activityTitle: Int
//		get() = R.string.scan_device
//
//	fun bindUI(rootView: View?) {
//		super.bindUI(rootView)
//		val theme = SharedPreferencesUtil.getData("theme", 0) as Int
//		ImmersionBar.with(this).titleBar(R.id.llt_title)
//			.statusBarDarkFont(if (theme == 0) true else false, 0.2f)
//			.keyboardEnable(true)
//			.init()
//		mLoadingDialog = LoadingDialog(this)
//		datas = ArrayList<MBluetoothDevice>()
//		mRecyclerview = findViewById(R.id.recyclerview)
//		mBleDeviceAdpter = BleDeviceAdpter(this)
//		mBleDeviceAdpter.setmOnClickListener(object : OnClickListener() {
//			fun onClick(item: MBluetoothDevice) {
//				if (mLoadingDialog != null) {
//					mLoadingDialog.show()
//				}
//				mBluetoothDevice = item.mBluetoothDevice
//				connect()
//			}
//		})
//		mRecyclerview!!.setHasFixedSize(true)
//		val layoutManager = LinearLayoutManager(this)
//		mRecyclerview!!.setLayoutManager(layoutManager)
//		mBleDeviceAdpter.setData(datas)
//		mRecyclerview!!.setAdapter(mBleDeviceAdpter)
//	}
//
//	private fun connect() {
//		if (mBluetoothDevice == null) {
//			return
//		}
//		handler2.removeCallbacks(runnable2)
//		handler2.postDelayed(runnable2, 20000)
//		val deviceAddress = mBluetoothDevice!!.getAddress()
//		if (TextUtils.isEmpty(deviceAddress)) {
//			val pair: Boolean = HidUitls.Pair(deviceAddress)
//			if (pair) {
//				HidUitls.connect(mBluetoothDevice)
//			}
//			return
//		}
//		HidUitls.SelectedDeviceMac = deviceAddress
//		val pair: Boolean = HidUitls.Pair(deviceAddress)
//		if (pair) {
//			HidUitls.connect(deviceAddress)
//		}
//	}
//
//	fun initData() {
//		val initDialog = SharedPreferencesUtil.getData("initDialog", 0) as Int
//		if (initDialog == 0) {
//			AlertDialog(this).init()
//				.setTitle("声明")
//				.setCancelable(true)
//				.setMsg(
//					"由于本软件的蓝牙遥控功能是基于HID协议实现，但是部分手机厂家移除了hid模块，" +
//							"所以导致了不兼容情况的出现。如果你的" +
//							"手机出现了闪退等问题，大概率是该机型不被支持。\n\n" +
//							"更多好玩软件：www.wnkong.com"
//				)
//				.setPositiveButton("了解，不再提示！",
//					View.OnClickListener { SharedPreferencesUtil.putData("initDialog", 1) }).show()
//		}
//	}
//
//	protected fun onStart() {
//		super.onStart()
//		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//		val intentFilter = IntentFilter()
//		intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
//		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
//		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
//		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
//		intentFilter.addAction("android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED")
//		registerReceiver(mReceiver, intentFilter)
//		if (!EventBus.getDefault().isRegistered(this)) {
//			EventBus.getDefault().register(this)
//		}
//		datas!!.clear()
//		mBleDeviceAdpter.notifyDataSetChanged()
//		val pairedDevices = mBluetoothAdapter.getBondedDevices()
//		val bluetoothDevices = Arrays.asList(*pairedDevices.toTypedArray<BluetoothDevice>())
//		for (device: BluetoothDevice in bluetoothDevices) {
//			val bean = MBluetoothDevice()
//			bean.mBluetoothDevice = device
//			bean.type = 1
//			addDevice(bean)
//		}
//		startDiscovery()
//	}
//
//	protected fun onStop() {
//		super.onStop()
//		unregisterReceiver(mReceiver)
//		if (EventBus.getDefault().isRegistered(this)) {
//			EventBus.getDefault().unregister(this)
//		}
//		cancelDiscovery()
//		if (mLoadingDialog != null) {
//			mLoadingDialog.dismiss()
//		}
//		if (alertDialog2 != null) {
//			alertDialog2.dismiss()
//		}
//		if (handler != null && runnable != null) {
//			handler.removeCallbacks(runnable)
//		}
//		if (handler2 != null && runnable2 != null) {
//			handler2.removeCallbacks(runnable2)
//		}
//	}
//
//	private fun startDiscovery() {
//		handler.removeCallbacks(runnable)
//		if (mBluetoothAdapter == null) {
//			return
//		}
//		if (mBluetoothAdapter!!.isDiscovering()) {
//			return
//		}
//		mBluetoothAdapter!!.startDiscovery()
//	}
//
//	@Subscribe(threadMode = ThreadMode.MAIN)
//	fun handleEvent(message: HidEvent) {
//		if (message.mtcpType === HidEvent.tcpType.onConnected) {
//			if (mLoadingDialog != null) {
//				mLoadingDialog.dismiss()
//			}
//			handler2.removeCallbacks(runnable2)
//			ToastUtils.showShort("连接成功")
//			Router.newIntent(this).to(MainActivity::class.java).launch()
//			finish()
//		} else if (message.mtcpType === HidEvent.tcpType.onDisConnected) {
//			if (mLoadingDialog != null) {
//				mLoadingDialog.dismiss()
//			}
//			ToastUtils.showShort("连接失败")
//			handler2.removeCallbacks(runnable2)
//			showDisConnectDialog()
//		}
//	}
//
//	private fun cancelDiscovery() {
//		handler.removeCallbacks(runnable)
//		if (mBluetoothAdapter == null) {
//			return
//		}
//		if (!mBluetoothAdapter!!.isDiscovering()) {
//			return
//		}
//		mBluetoothAdapter!!.cancelDiscovery()
//		mBleDeviceAdpter = null
//	}
//
//	//定义广播接收
//	private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//		override fun onReceive(context: Context, intent: Intent) {
//			val action = intent.action
//			if ((action == BluetoothDevice.ACTION_FOUND)) {
//				val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//				val bean = MBluetoothDevice()
//				bean.mBluetoothDevice = device
//				if (device!!.getBondState() == BluetoothDevice.BOND_BONDED) {    //显示已配对设备
//					bean.type = 1
//				} else {
//					bean.type = 0
//				}
//				addDevice(bean)
//			} else if ((action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
//				//重新扫描
//				handler.postDelayed(runnable, 5000)
//			} else if ((BluetoothDevice.ACTION_BOND_STATE_CHANGED == action)) {
//				val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//				when (device!!.getBondState()) {
//					BluetoothDevice.BOND_BONDING -> Log.e("BlueToothTestActivity", "正在配对......")
//					BluetoothDevice.BOND_BONDED -> {
//						Log.e("BlueToothTestActivity", "完成配对")
//						connect()
//					}
//
//					BluetoothDevice.BOND_NONE -> Log.e("BlueToothTestActivity", "取消配对")
//					else -> {}
//				}
//			}
//		}
//	}
//
//	private fun addDevice(bean: MBluetoothDevice) {
//		var deviceFound = false
//		for (tmp: MBluetoothDevice in datas) {
//			if (tmp.mBluetoothDevice.getAddress().equals(bean.mBluetoothDevice.getAddress())) {
//				deviceFound = true
//			}
//		}
//		if (!deviceFound) {
//			datas!!.add(bean)
//			Collections.sort(datas)
//			mBleDeviceAdpter.notifyDataSetChanged()
//		}
//	}
//
//	private var alertDialog2: AlertDialog? = null
//	private fun showDisConnectDialog() {
//		if (alertDialog2 == null) {
//			alertDialog2 = AlertDialog(this)
//			alertDialog2
//				.init()
//				.setMsg("蓝牙似乎已经连接上了,但是APP接收不到系统反馈,你可以重启APP,或者点击重新连接")
//				.setPositiveButton("重启APP", View.OnClickListener { finish() })
//				.setNegativeButton("重新连接", View.OnClickListener { connect() })
//		}
//		alertDialog2.show()
//	}
//}
