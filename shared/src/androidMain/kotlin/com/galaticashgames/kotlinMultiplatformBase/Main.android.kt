package com.galaticashgames.kotlinMultiplatformBase

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.core.content.ContextCompat
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// GOAL: All logic specific to Android Devices

// TODO [CLEANUP]: Could be a data class?
// Container for all data/classes related to devices running IOS
class AndroidDevice(a : Activity) : Device(AndroidScreenOrientationController(a), AndroidFileReader()) {}

class AndroidFileReader() : FileReader() {
    override val directory: String = "" // May not be needed?
    override fun getFilePath(): String? {
        return null
    }

    override fun readFile(filename: String): String? {
        val fileLocation = directory + filename

        // Attempt to read data from the file at the given filepath
        try {
            // TODO [EXISTING FEATURE]: Read from a file, return read data or null
            val readData = ""

            throw Exception("Not implemented")
            return readData
        }
        // TODO [ERROR HANDLING]: Add exceptions and Log errors (permission, file not found, etc)
        catch (e: Exception) {
            if(e.message != null) {
                DEBUG_LOGGER.i { "${e.message}" }
            }
            else {
                DEBUG_LOGGER.i { "[Android FILEREADER]: Could not read file from $fileLocation" }
            }
        }

        // Data could not be read
        return null
    }

    override fun writeFile(filename: String, data: String?): Boolean {
        val fileLocation = directory + filename

        // Attempt to write data to the file at the given filepath
        try {
            // TODO [I/O]: Reformat data to save
            val writeData = data

            // TODO [EXISTING FEATURE]: Write to a file, return if successful

            throw Exception("Not implemented")
            return true
        }
        // TODO [ERROR HANDLING]: Add exceptions and Log errors (permission, file not found, etc)
        catch (e: Exception) {
            if(e.message != null) {
                DEBUG_LOGGER.i { "${e.message}" }
            }
            else {
                DEBUG_LOGGER.i { "[Android FILEREADER]: Could not read file from $fileLocation" }
            }
        }

        // Data was not saved
        return false
    }
}

// Returns a FileReader for the application to use
actual fun getFileReader() : FileReader {
    return AndroidFileReader()
}

// TODO [OS ERROR]: Need access to current Android Activity to lock/unlock screen orientation
class AndroidScreenOrientationController(
    private val activity: Activity
    ) : ScreenOrientationController() {
    override fun lock(orientation: ScreenOrientation) {
        ContextCompat.getMainExecutor(activity).execute {
            activity.requestedOrientation = when (orientation) {
                ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                ScreenOrientation.UNSPECIFIED -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }
    override fun unlock() {
        ContextCompat.getMainExecutor(activity).execute {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

// Getting DateTime
class AndroidClock() : Clock {
    override fun now(): Instant {
        return Clock.System.now()
    }
}

actual fun getSystemClock(): Clock {
    return AndroidClock()
}