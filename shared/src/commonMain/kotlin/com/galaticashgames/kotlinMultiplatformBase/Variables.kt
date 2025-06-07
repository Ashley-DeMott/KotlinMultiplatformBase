package com.galaticashgames.kotlinMultiplatformBase

import kotlinx.serialization.Serializable
import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

// Loggers to report debug messages or errors, have specific tags to make them easier to find
val DEBUG_LOGGER = Logger(loggerConfigInit(platformLogWriter()),"[App] Debug")
val ERROR_LOGGER = Logger(loggerConfigInit(platformLogWriter()),"[App] Error")