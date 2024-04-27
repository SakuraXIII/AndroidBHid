package com.sy.bhid.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.sy.bhid.MyApplication
import java.util.Timer
import java.util.TimerTask
import kotlin.math.min


fun Modifier.keepSquare() = layout { measurable, constraints ->
	val placeable = measurable.measure(constraints)
	val size = min(placeable.width, placeable.height)
	layout(size, size) {
		placeable.place(0, 0)
	}
}


fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.then(
	clickable(interactionSource = MutableInteractionSource(), indication = null, onClick = onClick)
)

fun Modifier.fillMaxSize(width: Float, height: Float): Modifier = this.then(
	fillMaxWidth(width).fillMaxHeight(height)
)


/**
 * 工具类
 * @author SY
 * @since 2022-01-22 22:04
 **/
object Utils {

	fun showLog(log: String, tag: String = "SYTAG", level: Int = Log.INFO) {
		val current = Throwable().stackTrace[2]
		val fileName = current.fileName
		val lineNumber = current.lineNumber
		val decorLog = "($fileName:$lineNumber) ===> $log"
		when (level) {
			Log.VERBOSE -> Log.v(tag, decorLog)
			Log.DEBUG -> Log.d(tag, decorLog)
			Log.INFO -> Log.i(tag, decorLog)
			Log.WARN -> Log.w(tag, decorLog)
			Log.ERROR -> Log.e(tag, decorLog)
			Log.ASSERT -> Log.wtf(tag, decorLog)
		}

	}

	fun checkAndRequestPermissions(activity: Activity, PERMISSIONS: Array<String>) {
		for (permission in PERMISSIONS) {
			if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(activity, PERMISSIONS, 10);
				return;
			}
		}
	}

	fun DelayTask(runnable: Runnable, delay: Int, runonce: Boolean): TimerTask {
		val timer = Timer()
		val task: TimerTask = object : TimerTask() {
			override fun run() {
				runnable.run()
				if (runonce) {
					cancel()
				}
			}
		}
		timer.schedule(task, delay.toLong())
		return task
	}
	fun toHexStringForLog(data: ByteArray?): String {
		val sb = StringBuilder()
		if (data != null) {
			for (i in data.indices) {
				var tempHexStr = Integer.toHexString(data[i].toInt() and 0xff) + " "
				tempHexStr = if (tempHexStr.length == 2) "0$tempHexStr" else tempHexStr
				sb.append(tempHexStr)
			}
		}
		return sb.toString()
	}
}


fun dpTopx(value: Float): Int {
	val scale = MyApplication.context.resources.displayMetrics.density
	return (value * scale + 0.5f).toInt()
}

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
	Toast.makeText(MyApplication.context, this, duration).show()
}

fun <T> intent(packageContext: Context, serviceClass: Class<T>): Intent =
	Intent(packageContext, serviceClass)

inline fun <reified T> MyIntent(context: Context): Intent = intent(context, T::class.java)

fun <T : ViewModel> createModel(
	context: ViewModelStoreOwner,
	viewModelClass: Class<T>
): T = ViewModelProvider(
	context,
	ViewModelProvider.NewInstanceFactory()
).get(viewModelClass)

inline fun <reified T : ViewModel> createModel(context: ViewModelStoreOwner): T =
	createModel(context, T::class.java)