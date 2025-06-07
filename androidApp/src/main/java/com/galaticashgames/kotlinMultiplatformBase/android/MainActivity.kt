package com.galaticashgames.kotlinMultiplatformBase.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.galaticashgames.kotlinMultiplatformBase.AndroidDevice
import com.galaticashgames.kotlinMultiplatformBase.Main

class MainActivity : ComponentActivity() {
    private val device = AndroidDevice(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Main(device)
        }
    }

    // DEBUG: Use log to identify when this function is called and if things can be done after the super
    // Called when.. the app is stopped
    override fun onStop() {
        println("Before Android MainActivity super onSTOP")
        super.onStop()
        println("After Android MainActivity super onSTOP")
    }

    // DEBUG: Use log to identify when this function is called
    // Called when..
    override fun onDestroy() {
        println("Before Android MainActivity super onDESTROY")
        super.onDestroy()
        println("After Android MainActivity super onDESTROY")
    }
}