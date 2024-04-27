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

	init {
		throw UnsupportedOperationException("u can't instantiate me...")
	}


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
	 * @param text 文本
	 */
	fun showShortSafe(text: CharSequence) {
		sHandler.post { show(text, Toast.LENGTH_SHORT) }
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
	 * @param text 文本
	 */
	fun showLongSafe(text: CharSequence) {
		sHandler.post { show(text, Toast.LENGTH_LONG) }
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
	 * 显示短时吐司
	 *
	 * @param text 文本
	 */
	fun showShort(text: CharSequence) {
		show(text, Toast.LENGTH_SHORT)
	}


	/**
	 * 显示短时吐司
	 *
	 * @param format 格式
	 * @param args   参数
	 */
	fun showShort(format: String, vararg args: Any?) {
		show(format, Toast.LENGTH_SHORT, args)
	}

	/**
	 * 显示长时吐司
	 *
	 * @param text 文本
	 */
	fun showLong(text: CharSequence) {
		show(text, Toast.LENGTH_LONG)
	}


	/**
	 * 显示长时吐司
	 *
	 * @param format 格式
	 * @param args   参数
	 */
	fun showLong(format: String, vararg args: Any?) {
		show(format, Toast.LENGTH_LONG, args)
	}


	/**
	 * 显示吐司
	 *
	 * @param format   格式
	 * @param duration 显示时长
	 * @param args     参数
	 */
	private fun show(format: String, duration: Int, vararg args: Any) {
		show(String.format(format, *args), duration)
	}

	/**
	 * 显示吐司
	 *
	 * @param text     文本
	 * @param duration 显示时长
	 */
	private fun show(text: CharSequence, duration: Int) {
		cancel()
		sToast = Toast.makeText(AppUtils.getContext(), text, duration)
		sToast!!.setGravity(gravity, xOffset, yOffset)
		sToast!!.show()
	}

	/**
	 * 取消吐司显示
	 */
	private fun cancel() {
		if (sToast != null) {
			sToast!!.cancel()
			sToast = null
		}
	}

}