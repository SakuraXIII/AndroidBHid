package com.sy.bhid.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.WindowManager


class ScreenUtils {
	private fun ScreenUtils() {
		throw UnsupportedOperationException("u can't instantiate me...")
	}

	/**
	 * 获取屏幕的宽度（单位：px）
	 *
	 * @return 屏幕宽px
	 */
	fun getScreenWidth(context: Context): Int {
		val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val dm = DisplayMetrics() // 创建了一张白纸
		windowManager.defaultDisplay.getMetrics(dm) // 给白纸设置宽高
		return dm.widthPixels
	}

	/**
	 * 获取屏幕的高度（单位：px）
	 *
	 * @return 屏幕高px
	 */
	fun getScreenHeight(context: Context): Int {
		val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		val dm = DisplayMetrics() // 创建了一张白纸
		windowManager.defaultDisplay.getMetrics(dm) // 给白纸设置宽高
		return dm.heightPixels
	}


	/**
	 * 获取当前屏幕截图，包含状态栏
	 *
	 * @param activity activity
	 * @return Bitmap
	 */
	fun captureWithStatusBar(activity: Activity): Bitmap {
		val view = activity.window.decorView
		view.setDrawingCacheEnabled(true)
		view.buildDrawingCache()
		val bmp = view.drawingCache
		val dm = DisplayMetrics()
		activity.windowManager.defaultDisplay.getMetrics(dm)
		val ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
		view.destroyDrawingCache()
		return ret
	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 *
	 * @param activity activity
	 * @return Bitmap
	 */
	fun captureWithoutStatusBar(activity: Activity): Bitmap {
		val view = activity.window.decorView
		view.setDrawingCacheEnabled(true)
		view.buildDrawingCache()
		val bmp = view.drawingCache
		val statusBarHeight = getStatusBarHeight(activity)
		val dm = DisplayMetrics()
		activity.windowManager.defaultDisplay.getMetrics(dm)
		val ret = Bitmap.createBitmap(bmp, 0, statusBarHeight, dm.widthPixels, dm.heightPixels - statusBarHeight)
		view.destroyDrawingCache()
		return ret
	}

	/**
	 * 获取状态栏高度
	 *
	 * @param context context
	 * @return 状态栏高度
	 */
	fun getStatusBarHeight(context: Context): Int {
		val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
		return context.resources.getDimensionPixelSize(resourceId)
	}

	/**
	 * 设置全屏
	 * @param context
	 */
	fun setFullScreen(context: Context?) {
		if (context is Activity) {
			val activity = context
			val params = activity.window.attributes
			params.flags = params.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
			activity.window.setAttributes(params)
			activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		}
	}
}