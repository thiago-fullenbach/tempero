package br.com.thiago.tempero.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import br.com.thiago.tempero.R

// Dependência: androidx.compose.ui:ui-text-google-fonts
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val Brand = FontFamily(
    Font(GoogleFont("Fraunces"), provider, FontWeight.Medium)
)
val Plain = FontFamily(
    Font(GoogleFont("Roboto Flex"), provider, FontWeight.Normal),
    Font(GoogleFont("Roboto Flex"), provider, FontWeight.Medium)
)

private val base = Typography()

val AppTypography = Typography(
    displaySmall = base.displaySmall.copy(fontFamily = Brand, fontWeight = FontWeight.Medium, fontSize = 36.sp, lineHeight = 44.sp),
    headlineMedium = base.headlineMedium.copy(fontFamily = Brand, fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = base.headlineSmall.copy(fontFamily = Brand, fontWeight = FontWeight.Medium, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = base.titleLarge.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = base.titleMedium.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = base.titleSmall.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = base.bodyLarge.copy(fontFamily = Plain, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = base.bodyMedium.copy(fontFamily = Plain, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall = base.bodySmall.copy(fontFamily = Plain, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = base.labelLarge.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = base.labelMedium.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = base.labelSmall.copy(fontFamily = Plain, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)