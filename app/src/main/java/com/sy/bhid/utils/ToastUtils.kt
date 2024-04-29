package com.sy.bhid.utils

import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast

/**
 * <pre>
 * desc  : 吐司相关工具类
</pre> *
 */
object ToastUtils {

    private var sToast: Toast? = null
    private var gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
    private var xOffset = 0
    private var yOffset = (64 * AppUtils.getContext().resources.displayMetrics.density + 0.5).toInt()

    private val sHandler: Handler = Handler(Looper.getMainLooper())

    /**
     * 设置吐司位置
     *
     * @param gravity 位置
     * @param xOffset x偏移
     * @param yOffset y偏移
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        this.gravity = gravity
        this.xOffset = xOffset
        this.yOffset = yOffset
    }


    /**
     * 安全地显示短时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showShortSafe(format: String, vararg args: Any?) {
        sHandler.post { show(format, Toast.LENGTH_SHORT, args) }
    }


    /**
     * 安全地显示长时吐司
     *
     * @param format 格式
     * @param args   参数
     */
    fun showLongSafe(format: String, vararg args: Any?) {
        sHandler.post { show(format, Toast.LENGTH_LONG, args) }
    }


    /**
     * 显示吐司
     *
     * @param format   格式
     * @param duration 显示时长
     * @param args     参数
     */
    private fun show(format: String, duration: Int, vararg args: Any) {
        _show(String.format(format, *args), duration)
    }

    /**
     * 显示吐司
     *
     * @param text     文本
     * @param duration 显示时长
     */
    private fun _show(text: CharSequence, duration: Int) {
        _cancel()
        sToast = Toast.makeText(AppUtils.getContext(), text, duration)
        sToast!!.show()
    }

    /**
     * 取消吐司显示
     */
    private fun _cancel() {
        if (sToast != null) {
            sToast!!.cancel()
            sToast = null
        }
    }

}