package com.galaticashgames.kotlinMultiplatformBase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog

// GOAL: This file contains functions used to make building the ui easier
// TODO [WORKFLOW CLEANUP]: Create more Composables to make UI creation easier and reduce duplicate code
//  Make reusable functions that build upon Composable functions to make a simpler interface (Show everything with the same modifier, TextStyle, etc)
// TODO [CLEANUP]: Should this be a class? Might be fine as just public helper functions for Main.kt. Still should limit scope/visibility

//region Linking Theme Colors
// Better link theme colors together (background color and foreground/text color)

// Use UIColor to get Colors from the MaterialTheme
enum class UIColor { Primary, Secondary, Tertiary, Background, Surface }

// Get the background and foreground Colors given the UIColor (based on MaterialTheme colors)
@Composable
fun getBGColor(color: UIColor) : Color {
    return when(color) {
        UIColor.Primary -> MaterialTheme.colorScheme.primary
        UIColor.Secondary -> MaterialTheme.colorScheme.secondary
        UIColor.Tertiary -> MaterialTheme.colorScheme.tertiary
        UIColor.Background -> MaterialTheme.colorScheme.background
        UIColor.Surface -> MaterialTheme.colorScheme.surface
    }
}

@Composable
fun getTextColor(color: UIColor) : Color {
    return when(color) {
        UIColor.Primary -> MaterialTheme.colorScheme.onPrimary
        UIColor.Secondary -> MaterialTheme.colorScheme.onSecondary
        UIColor.Tertiary -> MaterialTheme.colorScheme.onTertiary
        UIColor.Background -> MaterialTheme.colorScheme.onBackground
        UIColor.Surface -> MaterialTheme.colorScheme.onSurface
    }
}

//region Linking colors to Composable objects (Text, Box, etc)

// Define a CompositionLocal for the style
/*val test = compositionLocalOf { TextStyle(color = Color.White) } */

// Get CompositionLocal based on UIColor
@Composable
fun getBGStyle(uiColor : UIColor) : ProvidableCompositionLocal<Modifier> {
    val bgColor = getBGColor(uiColor)
    return compositionLocalOf {
        Modifier.background(bgColor)
    }
}
@Composable
fun getTextStyle(uiColor : UIColor) : ProvidableCompositionLocal<TextStyle> {
    val textColor = getTextColor(uiColor)
    return compositionLocalOf {
        TextStyle(color = textColor)
    }
}

// Show Composables using the text and bg colors from UIColor (color and onColor)
@Composable
fun UsingColor(uiColor : UIColor, content: @Composable () -> Unit) {
    val sectionStyle = getBGStyle(uiColor)
    val textStyle = getTextStyle(uiColor)

    // Provide the style to the composition tree
    CompositionLocalProvider(sectionStyle provides Modifier, textStyle provides TextStyle()) {
        Column(sectionStyle.current) {
            // ERROR: Not all Composables have style

            content()
            //content(style = textStyle.current)
            //Text("test", style = textStyle.current)
        }
    }
}

@Composable
fun showStyledTextC(uiColor : UIColor, text: String, content : @Composable (style :TextStyle) -> Unit) {
    val textStyle = getTextStyle(uiColor)
    CompositionLocalProvider(textStyle provides TextStyle()) {
        content(textStyle.current)
        //Text(text, style = textStyle.current)
    }
}

@Composable
fun showStyledText(text : String, uiColor : UIColor) {
    val textStyle = getTextStyle(uiColor)
    CompositionLocalProvider(textStyle provides TextStyle()) {
        Text(text, style = textStyle.current)
    }
}
//endregion
//endregion

//region Creating Composables of Set Sizes
// Sizes for different ui elements, specifics decided within each function
enum class UISize {XSmall, Small, Medium, Large, XLarge}

// Creates a button given the message, onClick event, and Button style
@Composable
fun CreateButton(msg: String, onClick: () -> Unit, size: UISize = UISize.Medium, color: UIColor = UIColor.Primary) {
    // Values determined by the button's size
    val width: Dp
    val height: Dp
    val textStyle : TextStyle
    when(size) {
        UISize.XSmall -> {
            width = 100.dp
            height = 40.dp
            textStyle = MaterialTheme.typography.bodySmall
        }
        UISize.Small -> {
            width = 125.dp
            height = 50.dp
            textStyle = MaterialTheme.typography.bodySmall
        }
        UISize.Medium -> {
            width = 200.dp
            height = 50.dp
            textStyle = MaterialTheme.typography.bodyMedium
        }
        UISize.Large -> {
            width = 250.dp
            height = 50.dp
            textStyle = MaterialTheme.typography.bodyMedium
        }
        UISize.XLarge -> {
            width = 250.dp
            height = 50.dp
            textStyle = MaterialTheme.typography.bodyMedium
        }
    }

    // Use uiColor to get Colors from the MaterialTheme
    val bgColor = getBGColor(color)
    val textColor = getTextColor(color)

    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(width).height(height),

        // The colors used for the button
        colors = ButtonDefaults.buttonColors(containerColor = bgColor)
    ) {
        Text(msg, color = textColor, style = textStyle)
    }
}

// GOAL: Place the given content into a @Composable container (Box, Column, Row, etc)
enum class ContainerType {Box, Column, Row}
@Composable
fun CreateSection(container: ContainerType, size: UISize, modifier: Modifier, content: () -> Unit) {
    // uiSize - give the element a strict width/height? or just more/less padding around content? Would also need to figure out how to handle content overflow
    when(container) {
        ContainerType.Box -> {
            Box(modifier = modifier) {
                content()
            }
        }
        ContainerType.Column -> {
            Column(modifier = modifier) {
                content()
            }
        }
        ContainerType.Row -> {
            Row(modifier = modifier) {
                content()
            }
        }
    }
}
//endregion

// The app header which shows the current screen's name and allows the user to return to the menu
@Composable
fun AppHeader(pageName: String, returnState : AppState, isDataNotSaved : Boolean, updateAppState: (AppState) -> Unit) {
    var clicked by remember { mutableStateOf(false)}

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Button to allow user to return to a previous state
        CreateButton(returnState.toString(), { clicked = true }, UISize.Small, UIColor.Background)

        // Name of the current page
        Text(pageName, modifier = Modifier.padding(5.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
    }

    // If the user has chosen to exit the current mode,
    if(clicked) {
        // If there is data that hasn't been saved,
        if(isDataNotSaved) {
            // Alert the user of the risk of unsaved data
            decisionPopup("Data will not be saved.\nAre you sure you want to exit?") { exit ->
                // If the user chooses to still exit,
                if(exit) {
                    // Move to ReturnState
                    updateAppState(returnState)
                } else {
                    // Hide the popup, allowing the user to return to their current mode,
                    clicked = false
                }
            }
        }
        else {
            // Move to the ReturnState
            updateAppState(returnState)
        }
    }
}

//region Dialog Windows
// Creates a popup window with a custom message and dismiss action
@Composable
fun alertPopup(msg : String, onDismiss : () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(25.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary).padding(15.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    msg,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.onSecondary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text("Close")
                }
            }
        }
    }
}

// Shows a popup where the user can respond yes or no to the given message, which is returned to onAction
@Composable
fun decisionPopup(msg : String, onAction : (Boolean) -> Unit) {
    Dialog(onDismissRequest = { onAction(false) }) {
        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(25.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
                    .padding(15.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    msg,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(.65f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { onAction(false) },
                        colors = ButtonColors(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("No")
                    }
                    Button(
                        onClick = { onAction(true) },
                        colors = ButtonColors(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text("Yes")
                    }
                }
            }
        }
    }
}
//endregion