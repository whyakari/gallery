package com.example.gallery.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Material Expressive Color Palette - Dynamic, Expressive, Personal
// Based on Material 3 with Expressive extensions for vibrant, emotional design

// Light Theme - Warm, inviting, expressive
val LightColorScheme = ColorScheme(
    // Primary - Expressive coral/peach (emotional, warm)
    primary = Color(0xFFE86C5A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF7A1E12),

    // Secondary - Expressive teal (calm, creative)
    secondary = Color(0xFF006D6E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9EF3F3),
    onSecondaryContainer = Color(0xFF002021),

    // Tertiary - Expressive purple (playful, imaginative)
    tertiary = Color(0xFF7C52A6),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF2E1051),

    // Error
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    // Background
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1D1C1E),

    // Surface
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1D1C1E),
    surfaceVariant = Color(0xFFF5F0F4),
    onSurfaceVariant = Color(0xFF4A444A),

    // Surface Tint (Expressive elevation)
    surfaceTint = Color(0xFFE86C5A),

    // Inverse
    inverseSurface = Color(0xFF333034),
    inverseOnSurface = Color(0xFFF4F0F4),
    inversePrimary = Color(0xFFFFB4A8),

    // Outline
    outline = Color(0xFF7A747A),
    outlineVariant = Color(0xFFCAC4CA),

    // Scrim
    scrim = Color.Black,
)

// Dark Theme - Deep, moody, expressive
val DarkColorScheme = ColorScheme(
    primary = Color(0xFFFFB4A8),
    onPrimary = Color(0xFF5C1B0E),
    primaryContainer = Color(0xFF7A1E12),
    onPrimaryContainer = Color(0xFFFFDAD4),

    secondary = Color(0xFF9EF3F3),
    onSecondary = Color(0xFF002021),
    secondaryContainer = Color(0xFF003838),
    onSecondaryContainer = Color(0xFF9EF3F3),

    tertiary = Color(0xFFD0BCFF),
    onTertiary = Color(0xFF2E1051),
    tertiaryContainer = Color(0xFF452A6A),
    onTertiaryContainer = Color(0xFFEADDFF),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF1D1C1E),
    onBackground = Color(0xFFEDEBEF),

    surface = Color(0xFF1D1C1E),
    onSurface = Color(0xFFEDEBEF),
    surfaceVariant = Color(0xFF4A444A),
    onSurfaceVariant = Color(0xFFCAC4CA),

    surfaceTint = Color(0xFFFFB4A8),

    inverseSurface = Color(0xFFEDEBEF),
    inverseOnSurface = Color(0xFF333034),
    inversePrimary = Color(0xFFE86C5A),

    outline = Color(0xFF948E94),
    outlineVariant = Color(0xFF4A444A),

    scrim = Color.Black,
)

// Expressive Extended Colors - For unique UI moments
object ExpressiveColors {
    // Vibrant accent colors for expressive moments
    val VibrantCoral = Color(0xFFFF6B5B)
    val VibrantTeal = Color(0xFF00D4D4)
    val VibrantPurple = Color(0xFFA855F7)
    val VibrantAmber = Color(0xFFF59E0B)
    val VibrantEmerald = Color(0xFF10B981)
    val VibrantRose = Color(0xFFF43F5E)
    val VibrantSky = Color(0xFF0EA5E9)
    val VibrantIndigo = Color(0xFF6366F1)

    // Expressive gradients
    val CoralToPeach = listOf(Color(0xFFFF6B5B), Color(0xFFFF9F7F))
    val TealToCyan = listOf(Color(0xFF006D6E), Color(0xFF00D4D4))
    val PurpleToPink = listOf(Color(0xFF7C52A6), Color(0xFFA855F7))
    val AmberToOrange = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
    val DeepOcean = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
    val Sunset = listOf(Color(0xFF7F1D1D), Color(0xFF991B1B), Color(0xFFF59E0B))

    // Semantic expressive colors
    val Celebration = Color(0xFFF59E0B)
    val Calm = Color(0xFF006D6E)
    val Creative = Color(0xFF7C52A6)
    val Energetic = Color(0xFFE86C5A)
    val Peaceful = Color(0xFF0EA5E9)
    val Playful = Color(0xFFF43F5E)
}
