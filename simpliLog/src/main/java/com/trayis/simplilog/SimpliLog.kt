package com.trayis.simplilog

import android.content.Context
import android.net.Uri
import android.os.Parcelable

/**
 * @author mudesai (Mukund Desai)
 * @created on 3/6/19
 */
class SimpliLog {

    companion object {

        fun initialize(context: Context, vararg tags: String) {
            Logging.initLogging(context, tags)
        }

        fun <P : Parcelable> setAdditionalData(data: P) {
            Logging.setAdditionalParameters(data)
        }

        fun addTags(vararg tags: String) {
            Logging.addTags(tags)
        }

        fun setTags(tags: Array<out String>) {
            Logging.setTags(tags)
        }

        fun seturi(uri: Uri) {
            Logging.setUri(uri)
        }

        fun removeTags(tags: Array<out String>) {
            Logging.removeTags(tags)
        }

        fun setLogLevel(logLevel: Int) {
            Logging.setLogLevel(logLevel)
        }

    }

}