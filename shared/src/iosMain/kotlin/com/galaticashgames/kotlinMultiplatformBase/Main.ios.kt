package com.galaticashgames.kotlinMultiplatformBase

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// GOAL: All logic specific to iOS Devices

// Container for all data/classes related to devices running IOS
class IOSDevice() : Device(IOSScreenOrientationController(), IOSFileReader()) {}

class IOSFileReader() : FileReader() {
    override val directory: String = "" // May not be needed?
    override fun getFilePath(): String? {
        return null
    }

    override fun readFile(filename: String): String? {
        val fileLocation = directory + filename

        // Attempt to read data from the file at the given filepath
        try {
            val readData = ""

            // TODO [OS I/O]: Read from a file, return read data or null

            throw Exception("Not implemented")
            return readData
        }
        // TODO [ERROR HANDLING]: Add exceptions and Log errors (permission, file not found, etc)
        catch (e: Exception) {
            if(e.message != null) {
                DEBUG_LOGGER.i { "${e.message}" }
            }
            else {
                DEBUG_LOGGER.i { "[iOS FILEREADER]: Could not read file from $fileLocation" }
            }
        }

        // Data could not be read
        return null
    }

    override fun writeFile(filename: String, data: String?): Boolean {
        val fileLocation = directory + filename

        // Attempt to write data to the file at the given filepath
        try {
            // TODO [I/O]: Reformat for saving to a local file
            val writeData = data

            // TODO [OS I/O]: Write to a file, return if successful

            throw Exception("Not implemented")
            return true
        }
        // TODO [ERROR HANDLING]: Add exceptions and Log errors (permission, file not found, etc)
        catch (e: Exception) {
            if(e.message != null) {
                DEBUG_LOGGER.i { "${e.message}" }
            }
            else {
                DEBUG_LOGGER.i { "[iOS FILEREADER]: Could not write to file at $fileLocation" }
            }
        }

        // Data was not saved
        return false
    }
}

// Returns a FileReader for the application to use
actual fun getFileReader() : FileReader {
    return IOSFileReader()
}

//region Screen Orientation Controller
class IOSScreenOrientationController() : ScreenOrientationController() {
    override fun lock(orientation: ScreenOrientation) {
    // TODO [GRADLE OS]: Add imports and test
    /*
        val mask = when (orientation) {
            ScreenOrientation.PORTRAIT -> UIInterfaceOrientationMask.UIInterfaceOrientationMaskPortrait
            ScreenOrientation.LANDSCAPE -> UIInterfaceOrientationMask.UIInterfaceOrientationMaskLandscape
            ScreenOrientation.UNSPECIFIED -> UIInterfaceOrientationMask.UIInterfaceOrientationMaskAll
        }
        UIDevice.currentDevice.setValue(mask.rawValue, forKey = "orientation")
    */
    }

    override fun unlock() {
        //  UIDevice.currentDevice.setValue(UIInterfaceOrientation.UIInterfaceOrientationUnknown.value, forKey = "orientation")
    }
}

// Getting DateTime
class IosClock() : Clock {
    override fun now(): Instant {
        return Clock.System.now()
    }
}

actual fun getSystemClock(): Clock {
    return IosClock()
}