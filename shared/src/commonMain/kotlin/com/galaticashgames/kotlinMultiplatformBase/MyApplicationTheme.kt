package com.galaticashgames.kotlinMultiplatformBase

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//region Color Codes
// Grey and variations that are used
val darkGrey = Color(0xFF181818)
val darkerGrey = Color(0xFF121212)
val lightGrey = Color(0xFFD3D3D3)

// Blue and variations that are used
/* TODO [UI]: Update lighter and darker blue (based on guesstimated version)*/
val lighterBlue = Color(0xFF003BAD)
val darkerBlue = Color(0xFF001C53)
val darkBlue = Color(0xFF001035) /* Color picked */
val darkBlue1 = Color(0xFF002979) /* Guesstimated */

// Gold
val gold = Color(0xFFFFDE59)

val incorrectRed = Color(223, 102, 101)
val correctGreen = Color(146, 196, 125)

/*
//Default colors from base code, give names and use or ignore
val defaultColor = Color(0xFF03DAC5)
val defaultColor1 = Color(0xFF3700B3)
val defaultColor2 = Color(0xFFBB86FC)
val defaultColor3 =  Color(0xFF6200EE)
 */
//endregion

// TODO [EXTRA WORKFLOW]: Could make sub-themes? (different color schemes for certain sections?)
// The theme that is used to display the app
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    //region ColorScheme
    // Define the colors to be used in the application
    // TODO [UI]: Adjust and fine-tune the colors
    // There are even more colors (surfaceDim, surfaceTint, surfaceVariant, inversePrimary, inverseSurface, etc)
    // TODO [RESEARCH]: See what colors each Composable uses (if buttons use primary, if button:disabled uses background, etc)
    val colors =
        // If the user's device is in dark mode,
        if (darkTheme) {
        darkColorScheme(
            // Main colors of the scheme, darker than normal and no white
            primary = darkBlue,
            secondary = lighterBlue,
            tertiary = gold,
            background = darkerBlue, // Overall app background
            surface = darkGrey, // Color of flashcards and other surfaces

            // Text or icons on top of a color, ensures good contrast
            onPrimary = Color.White,
            onSecondary = Color.White,
            onTertiary = Color.Black,
            onBackground = Color.White,
            onSurface = lightGrey
            )
        }
        else {
            lightColorScheme(
                // Main colors of the scheme
                primary = lighterBlue,
                secondary = darkerBlue,
                tertiary = gold,
                background = darkBlue, // Overall app background
                surface = Color.White, // Color of flashcards and other surfaces

                // Text or icons on top of a color, ensures good contrast
                onPrimary = Color.White,
                onSecondary = Color.White,
                onTertiary = Color.Black,
                onBackground = Color.White,
                onSurface = Color.Black
            )
        }
    //endregion

    //region Typography
    // Define the text styling to be used in the application
    // TODO [UI]: Adjust and fine-tune the sizes, could change fontFamily for titles?
    // There are more text sections (label[Size Small-Large], display[Size], headline[Size])
    // TODO [RESEARCH]: See what Typography each Composable uses (if buttons use body or label, certain size, etc)
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
            /*fontSize = 16.sp*/
        ),

        bodyLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),

        titleMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 26.sp,
            letterSpacing = 0.sp
        ),

        // Only used by the Title on the Menu
        titleLarge = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 40.sp,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),

        labelSmall = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )
    //endregion

    // TODO [UI EXTRA]: For buttons, should more shapes be added? and use more descriptive names?
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    // Puts it all together, creating a MaterialTheme using the defined colors, typography, and shapes
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,

        // Shows the content with the above theme
        content = content
    )
}