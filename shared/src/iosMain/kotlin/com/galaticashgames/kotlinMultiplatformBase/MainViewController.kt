package com.galaticashgames.kotlinMultiplatformBase

import androidx.compose.ui.window.ComposeUIViewController

// TODO [OS]: Is this set up at a good time?
val device = IOSDevice()

// TODO [LOGIC]: When is MainViewController called? Just once or on every recompose?
fun MainViewController() = ComposeUIViewController {
    DEBUG_LOGGER.i { "[iOS] MainViewController called" }
    Main(device)
}