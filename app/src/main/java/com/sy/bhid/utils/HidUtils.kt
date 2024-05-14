package com.sy.bhid.utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.sy.bhid.bk.BluetoothKeyboard
import com.sy.bhid.data.HidReport
import com.sy.bhid.data.KeyMap
import java.util.Locale
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * HID配置以及发送报文相关功能
 */
object HidUtils {
    const val NAME = "SakuHx HID"
    const val DESCRIPTION = "SakuHx Keyboard"
    const val PROVIDER = "SakuHx"
    var HidDevice: BluetoothHidDevice? = null
    var BtDevice: BluetoothDevice? = null
    private var ModifierByte: Byte = 0x00
    private var KeyByte: Byte = 0x00
    val DESCRIPTOR = byteArrayOf(
        0x05.toByte(),
        0x01.toByte(),
        0x09.toByte(),
        0x02.toByte(),
        0xa1.toByte(),
        0x01.toByte(),
        0x09.toByte(),
        0x01.toByte(),
        0xa1.toByte(),
        0x00.toByte(),
        0x85.toByte(),
        0x01.toByte(),
        0x05.toByte(),
        0x09.toByte(),
        0x19.toByte(),
        0x01.toByte(),
        0x29.toByte(),
        0x03.toByte(),
        0x15.toByte(),
        0x00.toByte(),
        0x25.toByte(),
        0x01.toByte(),
        0x95.toByte(),
        0x03.toByte(),
        0x75.toByte(),
        0x01.toByte(),
        0x81.toByte(),
        0x02.toByte(),
        0x95.toByte(),
        0x01.toByte(),
        0x75.toByte(),
        0x05.toByte(),
        0x81.toByte(),
        0x03.toByte(),
        0x05.toByte(),
        0x01.toByte(),
        0x09.toByte(),
        0x30.toByte(),
        0x09.toByte(),
        0x31.toByte(),
        0x09.toByte(),
        0x38.toByte(),
        0x15.toByte(),
        0x81.toByte(),
        0x25.toByte(),
        0x7f.toByte(),
        0x75.toByte(),
        0x08.toByte(),
        0x95.toByte(),
        0x03.toByte(),
        0x81.toByte(),
        0x06.toByte(),
        0xc0.toByte(),
        0xc0.toByte(),
        0x05.toByte(),
        0x01.toByte(),
        0x09.toByte(),
        0x06.toByte(),
        0xa1.toByte(),
        0x01.toByte(),
        0x85.toByte(),
        0x02.toByte(),
        0x05.toByte(),
        0x07.toByte(),
        0x19.toByte(),
        0xE0.toByte(),
        0x29.toByte(),
        0xE7.toByte(),
        0x15.toByte(),
        0x00.toByte(),
        0x25.toByte(),
        0x01.toByte(),
        0x75.toByte(),
        0x01.toByte(),
        0x95.toByte(),
        0x08.toByte(),
        0x81.toByte(),
        0x02.toByte(),
        0x95.toByte(),
        0x01.toByte(),
        0x75.toByte(),
        0x08.toByte(),
        0x15.toByte(),
        0x00.toByte(),
        0x25.toByte(),
        0x65.toByte(),
        0x19.toByte(),
        0x00.toByte(),
        0x29.toByte(),
        0x65.toByte(),
        0x81.toByte(),
        0x00.toByte(),
        0x05.toByte(),
        0x08.toByte(),
        0x95.toByte(),
        0x05.toByte(),
        0x75.toByte(),
        0x01.toByte(),
        0x19.toByte(),
        0x01.toByte(),
        0x29.toByte(),
        0x05.toByte(),
        0x91.toByte(),
        0x02.toByte(),
        0x95.toByte(),
        0x01.toByte(),
        0x75.toByte(),
        0x03.toByte(),
        0x91.toByte(),
        0x03.toByte(),
        0xc0.toByte()
    )
    private var handler: Handler? = null
    private var singleThreadExecutor: ExecutorService? = null

    /**
     * 创建一个 handler，在线程中接收消息，发送报文数据
     */
    private fun reporttrans() {
        singleThreadExecutor = Executors.newSingleThreadExecutor()
        singleThreadExecutor!!.execute(Looper.myLooper()?.let {
            Runnable {
                Looper.prepare()
                handler = object : Handler(it) {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        val mHidReport: HidReport = msg.obj as HidReport
                        postReport(mHidReport)
                    }
                }
                Looper.loop()
            }
        })
    }

    /**
     * 封装报文格式，预处理
     */
    @SuppressLint("MissingPermission")
    private fun postReport(report: HidReport) {
        report.SendState = HidReport.State.Sending
        Utils.showLog(
            "ID:${report.reportId}, KEY:${report.key}, DEC:${report.data.contentToString()}, HEX:${Utils.toHexStringForLog(report.data)}",
            level = Log.DEBUG
        )
        val ret = HidDevice!!.sendReport(BtDevice, report.reportId, report.data)
        if (!ret) {
            report.SendState = HidReport.State.Failded
        } else {
            report.SendState = HidReport.State.Sended
        }
    }

    @SuppressLint("MissingPermission")
    fun exit() {
        handler?.looper?.quit()
        handler = null
        singleThreadExecutor?.shutdown()
        singleThreadExecutor = null
        HidDevice?.unregisterApp()
    }


    /**
     * 将报文数据处理为 Message，发送到 handler，在线程中处理。
     */
    fun addInputReport(inputReport: HidReport?) {
        if (handler == null || singleThreadExecutor == null) {
            reporttrans()
        }
        if (inputReport != null && handler != null) {
            val msg = Message()
            msg.obj = inputReport
            handler!!.sendMessage(msg)
        }
    }

    fun SendMouseReport(reportData: ByteArray) {
        val report = HidReport(HidReport.DeviceType.Mouse, 0x01, reportData)
        addInputReport(report)
    }

    private val MouseReport: HidReport = HidReport(HidReport.DeviceType.Mouse, 0x01, byteArrayOf(0, 0, 0, 0))

    fun MouseMove(dx: Int, dy: Int, wheel: Int, leftButton: Boolean, rightButton: Boolean, middleButton: Boolean) {
        var dx = dx
        var dy = dy
        var wheel = wheel
        if (MouseReport.SendState == HidReport.State.Sending) {
            return
        }
        if (dx > 127) dx = 127
        if (dx < -127) dx = -127
        if (dy > 127) dy = 127
        if (dy < -127) dy = -127
        if (wheel > 127) wheel = 127
        if (wheel < -127) wheel = -127
        if (leftButton) {
            MouseReport.data[0] = MouseReport.data[0] or 1
        } else {
            MouseReport.data[0] = MouseReport.data[0] and (1.inv()).toByte()
        }
        if (rightButton) {
            MouseReport.data[0] = MouseReport.data[0] or 2
        } else {
            MouseReport.data[0] = MouseReport.data[0] and (2.inv()).toByte()
        }
        if (middleButton) {
            MouseReport.data[0] = MouseReport.data[0] or 4
        } else {
            MouseReport.data[0] = MouseReport.data[0] and (4.inv()).toByte()
        }
        MouseReport.data[1] = dx.toByte()
        MouseReport.data[2] = dy.toByte()
        MouseReport.data[3] = wheel.toByte()
        addInputReport(MouseReport)
    }

    fun LeftBtnDown() {
        MouseReport.data[0] = MouseReport.data[0] or 1
        SendMouseReport(MouseReport.data)
    }

    fun LeftBtnUp() {
        MouseReport.data[0] = MouseReport.data[0] and (1.inv().toByte())
        SendMouseReport(MouseReport.data)
    }

    fun LeftBtnClick() {
        LeftBtnDown()
        Utils.DelayTask({ LeftBtnUp() }, 20, true)
    }

    fun LeftBtnClickAsync(delay: Int): TimerTask {
        return Utils.DelayTask({ LeftBtnClick() }, delay, true)
    }

    fun RightBtnDown() {
        MouseReport.data[0] = MouseReport.data[0] or 2
        SendMouseReport(MouseReport.data)
    }

    fun RightBtnUp() {
        MouseReport.data[0] = MouseReport.data[0] and 2.inv().toByte()
        SendMouseReport(MouseReport.data)
    }

    fun MidBtnDown() {
        MouseReport.data[0] = MouseReport.data[0] or 4
        SendMouseReport(MouseReport.data)
    }

    fun MidBtnUp() {
        MouseReport.data[0] = MouseReport.data[0] and 4.inv().toByte()
        SendMouseReport(MouseReport.data)
    }

    fun ModifierDown(UsageId: Byte): Byte {
        synchronized(HidUtils::class.java) { ModifierByte = (ModifierByte.toInt() or UsageId.toInt()).toByte() }
        return ModifierByte
    }

    fun ModifierUp(UsageId: Int): Byte {
        var UsageId = UsageId
        UsageId = UsageId.inv()
        synchronized(HidUtils::class.java) { ModifierByte = (ModifierByte.toInt() and UsageId).toByte() }
        return ModifierByte
    }

    /**
     * 按下指定按键
     */
    fun keyDown(key: String) {
        // 字符类型 HID 键码
        var usageStr = KeyMap.getKeys(key.lowercase(Locale.getDefault()))
        if (usageStr.startsWith("M")) {
            // M 表示 modifier 修饰键：Ctrl, Shift, Alt
            usageStr = usageStr.replace("M", "")
            synchronized(HidUtils::class.java) {
                // 表示按住修饰键
                val mod = ModifierDown(usageStr.toInt().toByte())
                SendKeyReport(byteArrayOf(mod, KeyByte), key)
            }
        } else {
            val code = usageStr.toInt().toByte()
            synchronized(HidUtils::class.java) {
                KeyByte = code
                SendKeyReport(byteArrayOf(ModifierByte, KeyByte), key)
            }
        }
    }

    /**
     * 松开指定键，直接发送报文 0 即可代表停止发送报文，即等效于调用 [stopKey]
     *
     * 但是这里需要检测松开的是否为修饰键，以实现组合键功能
     */
    fun keyUp(usageStr: String) {
        var usageStr = usageStr
        if (!TextUtils.isEmpty(usageStr)) {
            if (usageStr.startsWith("M")) {
                usageStr = usageStr.replace("M", "")
                synchronized(HidUtils::class.java) {
                    val mod = ModifierUp(usageStr.toInt())
                    SendKeyReport(byteArrayOf(mod, KeyByte))
                }
            } else {
                synchronized(HidUtils::class.java) {
                    KeyByte = 0
                    SendKeyReport(byteArrayOf(ModifierByte, KeyByte))
                }
            }
        }
    }

    /**
     * 表示停止发送报文，等同于keyup
     */
    fun stopKey() {
        SendKeyReport(byteArrayOf(0, 0))
    }

    /**
     * 封装报文
     *
     * @param reportData 报文数据部分
     * @param key 相应字符键
     */
    private fun SendKeyReport(reportData: ByteArray, key: String = "") {
        val report = HidReport(HidReport.DeviceType.Keyboard, 0x02, reportData, key)
        addInputReport(report)
    }
}
