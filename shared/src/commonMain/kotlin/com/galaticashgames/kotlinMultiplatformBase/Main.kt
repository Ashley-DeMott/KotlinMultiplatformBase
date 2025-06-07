package com.galaticashgames.kotlinMultiplatformBase

// TODO [PUBLISHING CLEANUP]: Cleanup imports to not have * versions (be more specific to only things that are needed)
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize

// TODO LIST:
//  - Device class
//  - FileReader implementation
//  - Create Node JS + MySQL server
//  - Networking implementation

// Each state corresponds to a page of the App
enum class AppState { Menu, Settings, Login, Create, Update, Feedback, Testing }

// DEBUG: Change AppState to start on a specific page
val STARTING_STATE = AppState.Menu //AppState.Login
// TODO [LOGIC]: Add logic to start on main menu if already logged in, else prompt login (pass userData or access class attribute)

// All data related to a user
@Serializable
data class User(
    val name: String
    // val id : Int // TODO [SERVER]: Primary key used in server, otherwise name would need to be unique. Can be just an int or more complex string (refer to how other apps implement this)
) {
    override fun toString(): String {
        return name
    }
}

// A class that represents the Application
class App(private val device : Device) {
    //region Variables
    // Padding to account for the device's top and bottom menu bars
    private var topPadding: Dp? = null
    private var bottomPadding: Dp? = null

    // TODO [CLEANUP]: Add to Device class
    // The FileReader that will be used to access files on the current device
    private var fileReader: FileReader = //device.getFileReader()
        getFileReader() // Returns Android/iOS specific FileReader

    // TODO [CLEANUP]: Add to Device class
    // The clock for the current device
    private val clock: Clock = getSystemClock() // Returns Android/iOS specific Clock
    // device.getClock()

    // Data relating to the currently logged in user TODO [SERVER]: Use to identify traffic to server/only logged in users can do certain requests
    private var currentUser : User? = null
    //endregion

    // Called when an instance of the class is created
    init {
        try {
            // Load the user data saved on this Device
            currentUser = fileReader.getUserData()

            // If no UserData can be found,
            if(currentUser == null) {
                // Prompt user to create an account
                // TODO [LOGIC]: Start ShowApp at Login screen
                DEBUG_LOGGER.i { "No user data found"}
            }
            else {
                DEBUG_LOGGER.i { "User successfully loaded"}
            }

            DEBUG_LOGGER.i { "[App]: Successfully setup App" }
        }
        catch (e : NullPointerException) {
            ERROR_LOGGER.i { "[App] ${e.message}" }
        }
        catch(e : Exception) {
            ERROR_LOGGER.i { "[App] ${e.message}" }
        }
    }

    // Setup Composable parts of the application, called after creation (must be called within a Composable function)
    @Composable
    fun setup() {
        // Calculate the values for the top and bottom padding (.asPaddingValues() is a @Composable)
        // device.getTopPadding(), device.getBottomPadding()
        topPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
        bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    }

    //region Composables
    // Entry point of the app, calls different Composable functions based on currentAppState
    @Composable
    fun ShowApp() {
        // The most important variable. Keeps track of what the app should be showing/doing
        var currentAppState by remember { mutableStateOf(STARTING_STATE) }
        DEBUG_LOGGER.i { "App state: $currentAppState" }   // NOTE: Everything within the function gets called again when a recompose happens

        var userData by remember { mutableStateOf(currentUser) } // TODO [OS]: Save changes before closing the app

        // NOTE: This is the highest level Composable, any display state data that needs to be remembered at the top level should be managed here
        // That includes reading data using FileReader --> setting it within current state data, which then can be passed down to specific screens, but then needs to be passed back here to update the current state

        // Box displaying over entire phone UI
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // TODO [UI]: Should match color of AppHeader
                //.background(Color.Red) // DEBUG: Show that status/navigation bars are outside of main content
        ) {
            // Main Content
            Box(
                modifier = Modifier
                    // Padding that ensures the status and navigation bars of the phone aren't covered
                    // TODO [TESTING UI]: Test with different screen sizes
                    .padding(
                        top = topPadding!!,
                        bottom = bottomPadding!!
                    )
                    .fillMaxSize()
            ) {
                // Switch case based on current AppState
                // TODO [CLEANUP]: Can lift some @Composable functionality out for commonly arranged pages if (currentAppState != Menu) { ShowAppHeader() }
                when (currentAppState) {
                    // Able to navigate to other pages based on AppState
                    AppState.Menu ->  ShowMenu { state: AppState -> currentAppState = state }
                    AppState.Settings -> ShowSettings { state: AppState -> currentAppState = state }

                    AppState.Login -> ShowLogin { state: AppState -> currentAppState = state }
                    AppState.Create -> ShowCreateUser { state: AppState -> currentAppState = state }
                    AppState.Update -> ShowUpdate { state: AppState -> currentAppState = state }

                    AppState.Feedback -> ShowFeedback { state: AppState -> currentAppState = state }
                    AppState.Testing -> ShowTest { state: AppState -> currentAppState = state }
                }
            }
        }
    }

    // DEBUG - A Page that shows various uiElements. Used to quickly view in emulation for testing
    // TODO [WORKFLOW]: Could use @Preview, but only works within Android (dependency issue)
    @Composable
    fun ShowTest(updateAppState: (AppState) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // Able to return back to normal app
            AppHeader("Testing", AppState.Menu, false, updateAppState)

            // Testing CreateButton helper function
            CreateButton("small, bg", {}, UISize.Small, UIColor.Background)
            CreateButton("medium, surface", {}, UISize.Medium, UIColor.Surface)
            CreateButton("large, primary", {}, UISize.Large, UIColor.Primary)
            CreateButton("medium, secondary", {}, UISize.Medium, UIColor.Secondary)
            CreateButton("medium, tertiary", {}, UISize.Medium, UIColor.Tertiary)
        }
    }

    @Composable
    fun ShowMenu(updateAppState: (AppState) -> Unit) {
        // All items in the Menu
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
        ) {
            // App logo and title
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Add Menu icon here
                //Image() {}
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                }
                Text(
                    "Kotlin Multiplatform Base",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // Menu selections
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75F)
                    .wrapContentSize(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                CreateButton("Login", {
                    updateAppState(AppState.Login)
                })          // Go to Login

                CreateButton("Testing", {
                    updateAppState(AppState.Testing)
                })          // Go to Testing

                CreateButton("Settings", {
                    updateAppState(AppState.Settings)
                })          // Go to Settings
            }
        }
    }

    // Show the Settings page
    @Composable
    fun ShowSettings(updateAppState: (AppState) -> Unit) {
        Column() {
            // Label this page "Settings" and allow navigation back to the Menu at any time
            //  There is no data that would be lost when updateAppState is called
            AppHeader("Settings", AppState.Menu, false, updateAppState)

            Text("${currentUser ?: "Null"}", color = MaterialTheme.colorScheme.onBackground)

            Button({updateAppState(AppState.Login)}) { Text("Login")}
            Button({updateAppState(AppState.Feedback)}) { Text("Submit Feedback")}
        }
    }

    //region User Login and Creation
    // Checks if the user-inputted string is valid (no extra whitespace/special characters, etc)
    private fun isValidString(input: String?): Boolean {
        // The value cannot be null
        input ?: return false

        // User must enter text
        if(input == "") {
            return false
        }

        // The value cannot have whitespace in it
        // TODO [ERROR HANDLING UI]: Change return type so a message/set error state can be shared with the User [cannot contain ' ']
        if(input.contains(" ") || input.contains("\t") || input.contains("\n")) {
            return false
        }

        // TODO [ERROR HANDLING INPUT]: Add more checks - special characters, etc

        // Passed all checks
        return true
    }

    // Checks if the user-entered password is valid
    private fun isUserPasswordValid(newPassword: String?): Boolean {
        // Must be a valid string input
        if(!isValidString(newPassword)) {
            return false
        }

        // Must pass complexity checks (not easy to guess, this would be for security reasons)
        // TODO [STRETCH]: Add password complexity checks (uppercase, number, symbol (but must be within utf-8/ASCII))

        return true
    }

    // Checks if the user-entered email is valid
    private fun isUserEmailValid(newEmail: String?): Boolean {
        if(!isValidString(newEmail)) {
            return false
        }

        // Must be an email TODO [STRETCH]: Add more checks
        if(!newEmail!!.contains("@")) {
            return false
        }

        // TODO [NETWORK]: Send database request to verify email/PK not already used, etc
        /*try {
        // Send createAccount request to server, returns error message if cannot reach server, user exists, etc
        } catch(e : Exception) {
        } */

        // Passed all checks
        return true
    }

    @Composable
    fun ShowLogin(updateAppState: (AppState) -> Unit) {
        Column() {
            // Label this page "Login" and allow navigation back to the Settings page at any time
            //  There is no data that would be lost when updateAppState is called (currently separate from Create user/reset password)
            AppHeader("Login", AppState.Settings, false, updateAppState)

            // Username input field
            var username by remember { mutableStateOf("") }
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier
            )

            // Password input field
            var password by remember { mutableStateOf("") }
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
            )

            // TODO [UI]: Make Login button bigger while additional buttons are smaller/display as hyperlink text
            // If the user has submitted their input, allows user to login with their credentials
            var submit by remember { mutableStateOf(false) }
            Button( { submit = true } ) { Text("Login") }

            // Additional buttons
            Button( {updateAppState(AppState.Create)} ) { Text("Create new account")}
            Button( {updateAppState(AppState.Testing)} ) { Text("Reset Password")}

            // If the user wants to verify their credentials,
            if(submit) {
                // If the app is waiting for a response from the server
                var requestSent by remember { mutableStateOf(false)}

                // If the user's credentials have been verified by the server (and saved locally)
                var verified by remember { mutableStateOf(false)}
                if(verified) {
                    // Return the user to the Menu
                    updateAppState(AppState.Menu)
                }
                // Otherwise the server needs to be asked
                else {
                    // TODO [ASYNC LOGIC]: Send request, then wait for and handle a response. Is if/else needed?
                    // If not waiting for a server response
                    if(requestSent) {
                        // Send a request
                    }
                    // If the user is waiting for a server response,
                    else {
                        var response : String? = null
                        try {
                            // TODO [UI THREADING]: While waiting for server response, show loading animation

                            // Server came back with a response
                            response = "Test"
                        }
                        // Catch if there is an error connecting to the server
                        catch (e: Exception) {
                            ERROR_LOGGER.i { "[JB] Login Error: ${e.message.toString()}" }
                            alertPopup("Error sending login request") {
                                submit = false      // Hide popup, allow user to edit their input
                                requestSent = false // Will need to send another request
                            }
                        }

                        // TODO [NETWORK LOGIC]: Check for the necessary data within the response
                        val validCredentials : Boolean = response != "Test" // DEBUG: Test valid/invalid server response

                        // If the user's credentials were verified,
                        if(validCredentials) {
                            // TODO [USER]: Save user data to device, also things like server access token

                            DEBUG_LOGGER.i { "[JB] User {username} logged in"}
                            verified = true
                        }
                        // User's credentials were rejected by the server
                        else {
                            alertPopup("Invalid credentials") {
                                submit = false      // Hide popup, allow user to edit their input
                                requestSent = false // Will need to send another request
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowCreateUser(updateAppState: (AppState) -> Unit) {
        // Inputs needed to create a user
        var email by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        // If the user has submitted their input
        var submit by remember { mutableStateOf(false) }

        Column() {
            // Label this page "Create Account" and allow navigation back to the Settings page at any time
            //  The user will have a "Are you sure you want to quit?" prompt to prevent accidental loss of data before updateAppState is called
            AppHeader("Create Account", AppState.Settings,true, updateAppState)

            // Email input field
            TextField(
                modifier = Modifier,
                value = email,
                label = { Text("Email") },
                onValueChange = { email = it }
            )

            // Username input field
            TextField(
                modifier = Modifier,
                value = username,
                label = { Text("Username") },
                onValueChange = { username = it }
            )

            // Password input field
            TextField(
                modifier = Modifier,
                value = password,
                label = { Text("Password") },
                onValueChange = { password = it }
            )

            // Change state of submit, allows user to create an account
            Button( { submit = true } ) { Text("Create") }

            // If the user has submitted their input,
            if(submit) {
                // TODO [ERROR HANDLING]: Checks can return an error String instead, returns "Pass" if valid
                // Checks if the user's input is valid
                // Check username
                if(!isValidString(username)) {
                    alertPopup("Invalid username") {
                        // TODO [LOGIC]: Other do on dismiss, Clear input?
                        submit = false
                    }
                }
                // Check password
                else if(!isUserPasswordValid(password)) {
                    alertPopup("Invalid password format") {
                        // TODO [LOGIC]: Other do on dismiss, Clear input?
                        submit = false
                    }
                }
                // Check email
                else if(!isUserEmailValid(email)) {
                    alertPopup("Invalid email/already in use") {
                        // TODO [LOGIC]: Other do on dismiss, Clear input?
                        submit = false
                    }
                }

                // Can create user
                else {
                    // DEBUG: Only creating the User's data locally
                    // Log the User in on this device by updating the UserInfo internal file
                    //saveUserData(User(name!!, password!!, email!!, mutableMapOf(), mutableMapOf()))
                    //currentUser = readUserData()

                    // User was created successfully, return to main menu on close
                    alertPopup("User \"$username\" created successfully") {
                        updateAppState(AppState.Menu)
                    }
                }
            }
        }
    }
    //endregion

    // TODO [FEATURE CLEANUP]: Rename for clarity
    @Composable
    fun ShowUpdate(updateAppState: (AppState) -> Unit) {
        Column() {
            // TODO [FEATURE]: Set to true if user has modified any TextInput (data != start data)
            var unsavedData by remember { mutableStateOf(false) }

            // Label this page "User" and allow navigation back to the Settings page at any time
            //  The user will have a "Are you sure you want to quit?" prompt to prevent accidental loss of data before updateAppState is called
            AppHeader("Settings : User", AppState.Settings, unsavedData, updateAppState)

            Text(
                "TODO: Update the user's information here, including the ability to reset password, update username/email, delete account, etc",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

    // Submit the user's feedback to be reviewed by the app developers
    @Composable
    fun ShowFeedback(updateAppState: (AppState) -> Unit) {
        Column() {
            // TODO [FEATURE]: Set to true if user has modified any TextInput (data != start data)
            var unsavedData by remember { mutableStateOf(true) } // DEBUG: should start as false

            // Label this page "Feedback" and allow navigation back to the Settings page at any time
            //  The user will have a "Are you sure you want to quit?" prompt to prevent accidental loss of data before updateAppState is called
            AppHeader("Submit Feedback", AppState.Settings, unsavedData, updateAppState)

            // Categories to sort User feedback // TODO [CLEANUP]: Make enum?
            val feedbackCategories = listOf(
                "Feature Request",
                "Login Issue",
                "Layout Issue",
                "Other Error"
            )

            // The currently selected Feedback Category
            var selectedCategory by remember { mutableStateOf("") }

            // The size of the TextField where the user can add additional comments
            var textFieldSize by remember { mutableStateOf(Size.Zero) }

            // If the dropdown is expanded or shrunk
            var dropdownExpanded by remember { mutableStateOf(false) }
            val icon = if(dropdownExpanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown

            // If the user has submitted their feedback
            var submit by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                // Modifier.padding(20.dp)
            ) {
                // TODO [UI]: Bad contrast, overwrite colors for OutlineTextField and DropdownMenu
                // Dropdown menu to select a category
                OutlinedTextField(value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    label = { Text("Category") },
                    trailingIcon = {
                        Icon(
                            icon,
                            "contentDescription",
                            Modifier.clickable { dropdownExpanded = !dropdownExpanded })
                    }
                )
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldSize.width.toDp() })

                ) {
                    feedbackCategories.forEach { label ->
                        DropdownMenuItem(text = {
                            Text(
                                text = label,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }, onClick = {
                            selectedCategory = label
                            dropdownExpanded = false
                        })
                    }
                }
            }

            // Textbox with user feedback
            var userFeedback by remember { mutableStateOf("") }
            OutlinedTextField(
                value = userFeedback,
                onValueChange = { userFeedback = it },
                label = { Text("Feedback Box") })

            // Allow user to submit their feedback
            Button(onClick = { submit = true}) {
                Text("Submit Feedback")
            }

            // If the user has submitted their feedback,
            if(submit) {
                // TEMP: Send as an email, could instead use tvm to send a request to the backend, which creates a ticket?
                /*
                // Create Intent to send as an email
                val i = Intent(Intent.ACTION_SEND)
                i.setType("message/rfc822")
                i.putExtra(Intent.EXTRA_EMAIL, arrayOf("galaticash.games@gmail.com"))

                // The email's subject is filled in with the Feedback category
                i.putExtra(Intent.EXTRA_SUBJECT, "App Feedback [$category]")

                // The email's text is filled in using the Feedback message
                i.putExtra(Intent.EXTRA_TEXT, "Feedback:\n$message")
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this,
                        "There are no email clients installed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
                // Inform the user that their feedback was successfully submitted
                alertPopup("[IN PROGRESS] TODO: Submit feedback to remote server" /* Feedback submitted successfully */) {
                    updateAppState(AppState.Menu)
                }
            }
        }
    }
    //endregion
}

// Composable function that displays the app, entry point for Android and iOS
@Composable
fun Main(d : Device) {
    // TODO [ERROR HANDLING]: If invalid FileReader/Device, app should crash/close (cannot continue, report "Invalid state" to the user/error message)
    //      Should have a class/function to handle errors. Sent the error message and shows it to the user and
    //      [LOGGING] Prompt the user if they want to send their most recent log to the developer in order to aid with solving the issue

    // Lock screen orientation
    d.soc.lock(ScreenOrientation.PORTRAIT)

    // Create App
    val a = App(d)

    // Composable setup
    a.setup()

    // Show the app using the custom application theme (MyApplicationTheme.kt)
    MyApplicationTheme {
        a.ShowApp()
    }
}