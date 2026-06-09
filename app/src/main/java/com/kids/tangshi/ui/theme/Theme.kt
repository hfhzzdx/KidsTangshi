package com.kids.tangshi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryYellow,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryYellowLight,
    onPrimaryContainer = OnPrimaryLight,
    secondary = SecondaryBlue,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryBlueLight,
    onSecondaryContainer = OnSecondaryLight,
    tertiary = TertiaryPink,
    onTertiary = OnPrimaryDark,
    tertiaryContainer = TertiaryPinkLight,
    onTertiaryContainer = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnPrimaryLight,
    surface = SurfaceLight,
    onSurface = OnPrimaryLight,
    error = ErrorLight,
    onError = OnPrimaryDark,
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryYellowDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryYellow,
    onPrimaryContainer = OnPrimaryDark,
    secondary = SecondaryBlueDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryBlue,
    onSecondaryContainer = OnPrimaryDark,
    tertiary = TertiaryPinkDark,
    onTertiary = OnPrimaryDark,
    tertiaryContainer = TertiaryPink,
    onTertiaryContainer = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnPrimaryDark,
    surface = SurfaceDark,
    onSurface = OnPrimaryDark,
    error = ErrorDark,
    onError = OnPrimaryLight,
)

@Composable
fun KidsTangshiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
