package com.galaticashgames.kotlinMultiplatformBase

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

// GOAL: Device is an interface that allows OS-specific data to be retrieved

// Container for all data/classes related to a device
abstract class Device(
    val soc : ScreenOrientationController, val fr : FileReader) {
    // TODO [OS]: Add getWidth()
}

// TODO [LOGIC]: Add read/write permissions as a parameter to FileReader class, can't use anyways if read permission == false
// The interface used to handle file interactions. Will be implemented differently depending on OS type (Android or iOS)
abstract class FileReader() {
    // A map of local data and the names of the files they are saved to
    private val filenames: Map<String, String> =
        mapOf(
            "UserData" to "userData.json",
        )

    // TODO [OS]: Determine where on the device the data is stored (need to store userData for this app), directory should be based on OS type, but may also depend on OS version?
    abstract val directory : String
    abstract fun getFilePath() : String?

    // DEBUG:
    // TODO [OS]: Probably only has one "storage" permission instead of read/write?
    // Returns if the user has granted the necessary permissions for reading/writing to files
    //abstract fun hasReadPermission() : Boolean
    //abstract fun hasWritePermission() : Boolean

    // Read data from a given file, returns data or null if there was an error
    abstract fun readFile(filename: String) : String?

    /* val jsonString = a.openFileInput(filenames["UserData"]).bufferedReader()
                .use(BufferedReader::readText) */

    // Write the given data to a given file, returns if successful
    abstract fun writeFile(filename: String, data: String?) : Boolean

    // Convert Hex Code String to a Color object
    private fun convertStringToColor(hexCode : String) : Color {
        // Get the red, green, and blue parts from the string (#FFFFFF)
        val red = hexCode.substring(1, 3).toInt(16)
        val green = hexCode.substring(3, 5).toInt(16)
        val blue = hexCode.substring(5).toInt(16)

        //JB_DEBUG_LOGGER.i {"Color $hexCode -> $red + $green + $blue"}
        // Return the Color made from the RGB
        return Color(red, green, blue)
    }

    // Convert from a Color to a String
    fun Color.toHexCode(): String {
        val red = this.red * 255
        val green = this.green * 255
        val blue = this.blue * 255
        return "#${red.toInt()}${red.toInt()}${red.toInt()}"
        // ERROR: Uses Java library
        //return String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())
    }

    //region User Data
    // TODO [OPTIMIZATION CLEANUP]: Probably don't need to save entire UserData over again, just change a portion?
    // Change parameter type to UserData/object being saved
    fun writeUserData(data : String) : Boolean {
        // TODO [EXISTING FEATURE]: Convert object -> json formatted String to save (use GSON)
        val writeData = data

        // Write to the file, returning if it was successful
        return writeFile(filenames["UserData"]!!, writeData)
    }

    // Return the currently saved user data. Returns null if no data saved
    fun getUserData() : User? {
        try {
            //readFile(filenames["UserData"]!!)
            return User("TODO:Load")
        }
        // TODO [ERROR HANDLING]: Only throw error if fatal? Or throw if fileNotFound (user not setup)?
        catch (e : Exception) { // TODO [ERROR HANDLING]: Add more catch clauses
            return null
        }
    }
    //endregion
}

// TODO [RESEARCH OS]: Use expect/actual and figure out function call to update permissions/access permission states
// Show the user a prompt to allow access
fun promptPermission() : Boolean {
    // Show OS built-in permission access prompt

    // Return if access was granted/changed
    return false
}
// TODO [OS PERMISSIONS]: If permissions haven't been given, show Grant Permission window (from OS), if user still doesn't grant permission, show "error, must have permission" screen
// TODO [RESEARCH OS PERMISSIONS]: Research what other apps do when permissions aren't given/are rescinded. Likely show error message and remain on main menu/don't allow user to use app
/*
@Composable
fun ShowInvalidPermissions() {
    //
    Box() {
        Text("Cannot read, [] permission not given")
    }
}*/

// TODO [CLEANUP]: Move inside Device class (or make data class)
// Get the OS specific FileReader
expect fun getFileReader() : FileReader

//region Device Rotation Lock
abstract class ScreenOrientationController {
    abstract fun lock(orientation: ScreenOrientation)
    abstract fun unlock()
}

enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE,
    UNSPECIFIED
}
//endregion

// TODO [OS TIME]: Get datetime from device
expect fun getSystemClock() : Clock