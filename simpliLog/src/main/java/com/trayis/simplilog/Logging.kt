package com.trayis.simplilog

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.util.Log

class Logging private constructor() {

    private abstract class Logger(context: Context, tags: Array<out String>, var uri: Uri? = null) {

        val mContext: Context = context.applicationContext

        internal val mTags = ArrayList<String>()

        internal var logLevel = Log.INFO

        private val mDeviceId: String
        private val mOsVersion: String
        private val mAppVersionName: String
        private var mDeviceModel: String

        internal var data: Parcelable? = null

        init {
            mTags.addAll(tags)

            mDeviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            mOsVersion = "Android-" + Build.VERSION.RELEASE
            mAppVersionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            mDeviceModel = getDeviceModel()

        }

        private fun getDeviceModel(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model != null && model.startsWith(manufacturer)) {
                model.convertToNameFormat()
            } else manufacturer.convertToNameFormat() + " " + model.convertToNameFormat()
        }

        abstract fun log(level: Int, tag: String, msg: String)
        abstract fun log(level: Int, tag: String, msg: String, e: Throwable?)

        fun formatMessage(msg: String): String? {
            var message =
                "Model: $mDeviceModel, Device Id : $mDeviceId, OS : $mOsVersion, App Version : $mAppVersionName, Message : $msg"

            data?.let {
                message += ", User Data : $data"
            }

            return message
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var logger: Logger? = null

        private const val MY_TAG = "Logging"

        internal fun initLogging(context: Context, tags: Array<out String>) {
            logger = object : Logger(context, tags) {
                override fun log(level: Int, tag: String, msg: String, e: Throwable?) {
                    log(level, tag, msg + "\n" + Log.getStackTraceString(e))
                }

                override fun log(level: Int, tag: String, msg: String) {
                    if (logLevel >= level) {
                        Log.println(level, tag, msg)
                        if (mTags.contains(tag)) {
                            val lMsg = logger?.formatMessage(msg) ?: msg
                            Log.println(level, tag, "Server Log : { $lMsg }")
                        }
                    }
                }
            }
        }

        internal fun setUri(uri: Uri) {
            logger?.uri = uri
        }

        internal fun <P : Parcelable> setAdditionalParameters(data: P) {
            logger?.data = data
        }

        internal fun setTags(tags: Array<out String>) {
            logger?.apply {
                mTags.clear()
                mTags.addAll(tags)
            }
        }

        internal fun addTags(tags: Array<out String>) {
            logger?.apply {
                for (tag in tags) {
                    if (!mTags.contains(tag)) {
                        mTags.add(tag)
                    }
                }
            }
        }

        internal fun removeTags(tags: Array<out String>) {
            logger?.mTags?.removeAll(tags)
        }

        internal fun setLogLevel(logLevel: Int) {
            logger?.let {
                it.logLevel = logLevel
                if (logLevel >= Log.WARN) {
                    Log.w(
                        Logging.MY_TAG,
                        "Caution!! You are setting log level as warning or beyond it, make sure this is not production build."
                    )
                }
            }
        }

        fun d(tag: String, msg: String) {
            logger?.apply {
                if (mContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    log(Log.DEBUG, tag, msg)
                }
            }
        }

        fun d(tag: String, msg: String?, e: Throwable?) {
            logger?.apply {
                if (mContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    log(Log.DEBUG, tag, msg ?: "", e)
                }
            }
        }

        fun e(tag: String, msg: String) {
            logger?.log(Log.ERROR, tag, msg)
        }

        fun e(tag: String, msg: String?, e: Throwable?) {
            logger?.log(Log.ERROR, tag, msg ?: "", e)
        }

        fun i(tag: String, msg: String) {
            logger?.log(Log.INFO, tag, msg)
        }

        fun i(tag: String, msg: String?, e: Throwable?) {
            logger?.log(Log.INFO, tag, msg ?: "", e)
        }

        fun v(tag: String, msg: String) {
            logger?.log(Log.VERBOSE, tag, msg)
        }

        fun v(tag: String, msg: String?, e: Throwable?) {
            logger?.log(Log.VERBOSE, tag, msg ?: "", e)
        }

        fun w(tag: String, msg: String) {
            logger?.log(Log.WARN, tag, msg)
        }

        fun w(tag: String, msg: String?, e: Throwable?) {
            logger?.log(Log.WARN, tag, msg ?: "", e)
        }
    }

}

private fun String.convertToNameFormat() = Character.toUpperCase(get(0)) + substring(1).toLowerCase()
