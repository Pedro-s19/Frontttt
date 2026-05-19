package com.example.finalpro.Ui1.Theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
fun FinalProTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary           = AccentPrimary,
        secondary         = AccentSecond,
        background        = BgPrimary,
        surface           = BgSurface,
        surfaceVariant    = BgCard,
        onPrimary         = Color.White,
        onSecondary       = Color.Black,
        onBackground      = TextPrimary,
        onSurface         = TextPrimary,
        onSurfaceVariant  = TextSecondary,
        outline           = Border,
        error             = ColorGasto
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = FinanceTypography,
        content     = content
    )
}