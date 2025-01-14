package presentation.uiComponents

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fredsroadtripstoryteller.shared.generated.resources.Res
import fredsroadtripstoryteller.shared.generated.resources.pet_me
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font


@OptIn(ExperimentalResourceApi::class)
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        // darkColorScheme(  // material3
        darkColors(
            primary = Color(0xFF0059CB),
            // onPrimary = Color(0xFFBBBBBB),
            onPrimary = Color(0xFFDDDDDD),
            secondary = Color(0xFF4cb2a2),
            onSecondary = Color(0xFFDDDDDD),
            // tertiary = Color(0xFF3700B3) // material3
            surface = Color(0xFF000000),
            onSurface = Color(0xFFBBBBBB),
            background = Color(0xFF333344),
            onBackground = Color(0xFFDDDDDD),
            onError = Color(0xFF222222),
            error = Color(0xFFFF0000)
        )
    } else {
        // lightColorScheme( // material3
        lightColors(
            primary = Color(0xFF0089FB),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFF00917d),
            onSecondary = Color(0xFFFFFFFF),
            // tertiary = Color(0xFF3700B3)
            surface = Color(0xFFCCCCCC),
            onSurface = Color(0xFF000000),
            background = Color(0xFF444444),
            onBackground = Color(0xFFDDDDDD),
            onError = Color(0xFF222222),
            error = Color(0xFFFF0000)
        )
    }

    // WOPR Font for Trial Countdown (From Commodore PET character set)
    val petMeFontFamily = FontFamily(
        Font(Res.font.pet_me, FontWeight.Medium, FontStyle.Normal)
    )

    val typography = Typography(
        // bodyMedium = TextStyle( // material3
        body2 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        overline = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 8.sp
        ),
        subtitle2 = TextStyle(
            fontFamily = if(LocalInspectionMode.current)
                    FontFamily.Monospace
                else
                    petMeFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 2.sp
        ),
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        // colorScheme = colors, // material3
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
