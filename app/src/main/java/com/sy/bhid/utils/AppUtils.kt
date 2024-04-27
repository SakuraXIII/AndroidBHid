package com.sy.bhid.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


@SuppressLint("StaticFieldLeak")
object AppUtils {
	private var context: Context? = null

	init {
		throw UnsupportedOperationException("u can't instantiate me...")
	}


	fun init(context: Context) {
		AppUtils.context = context
	}

	fun getContext(): Context {
		if (context != null) return context!!
		throw NullPointerException("u should init first")
	}


	fun getAppMsg(): String {
		return (getAppVersionCode().toString() + "/"
				+ getAppVersionName())
	}

	fun getPhoneMsg(): String {
		return (Build.BRAND + "/"
				+ Build.MODEL + "/"
				+ "anroid " + Build.VERSION.RELEASE)
	}

	private fun getAppVersionCode(): Int {
		return try {
			val pm = context!!.packageManager
			val pi = pm.getPackageInfo(context!!.packageName, 0)
			pi?.versionCode ?: -1
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
			-1
		}
	}

	private fun getAppVersionName(): String? {
		return try {
			val pm = context!!.packageManager
			val pi = pm.getPackageInfo(context!!.packageName, 0)
			pi?.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
			null
		}
	}
}