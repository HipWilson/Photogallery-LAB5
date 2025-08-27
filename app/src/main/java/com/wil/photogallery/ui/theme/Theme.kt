package com.wil.photogallery.ui.theme
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colores para el tema claro
private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF21005D),
    secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D192B),
    tertiary = androidx.compose.ui.graphics.Color(0xFF7D5260),
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFD8E4),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFF31111D),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFF410002),
    outline = androidx.compose.ui.graphics.Color(0xFF79747E),
    background = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onBackground = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFBFE),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFF313033),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFFF4EFF4),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFFD0BCFF),
    surfaceTint = androidx.compose.ui.graphics.Color(0xFF6750A4),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0),
    scrim = androidx.compose.ui.graphics.Color(0xFF000000),
)

// Colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFD0BCFF),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF381E72),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF4F378B),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
    secondary = androidx.compose.ui.graphics.Color(0xFFCCC2DC),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF332D41),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF4A4458),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    tertiary = androidx.compose.ui.graphics.Color(0xFFEFB8C8),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF492532),
    tertiaryContainer = androidx.compose.ui.graphics.Color(0xFF633B48),
    onTertiaryContainer = androidx.compose.ui.graphics.Color(0xFFFFD8E4),
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    onError = androidx.compose.ui.graphics.Color(0xFF690005),
    errorContainer = androidx.compose.ui.graphics.Color(0xFF93000A),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFDAD6),
    outline = androidx.compose.ui.graphics.Color(0xFF938F99),
    background = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surface = androidx.compose.ui.graphics.Color(0xFF1C1B1F),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFCAC4D0),
    inverseSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E5),
    inverseOnSurface = androidx.compose.ui.graphics.Color(0xFF313033),
    inversePrimary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    surfaceTint = androidx.compose.ui.graphics.Color(0xFFD0BCFF),
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF49454F),
    scrim = androidx.compose.ui.graphics.Color(0xFF000000),
)

@Composable
fun PhotoGalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Los colores dinámicos están disponibles en Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}