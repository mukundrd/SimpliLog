package com.trayis.simplilogapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.trayis.simplilog.Logging
import com.trayis.simplilog.SimpliLog

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SimpliLog.initialize(this, "Dashboard", "Wallet", "Offers", "Giftcard")

        val user = UserModel("First Name", "fiUser")
        SimpliLog.setAdditionalData(user)

        Logging.v(TAG, "in OnCreate");
        Logging.v("Dashboard", "User logged in");
    }

}
