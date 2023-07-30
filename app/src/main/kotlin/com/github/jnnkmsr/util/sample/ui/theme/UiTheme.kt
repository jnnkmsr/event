/*
 * Copyright 2023 Jannik Möser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jnnkmsr.util.sample.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Application UI theme.
 *
 * @param useDarkTheme Whether the theme should use a dark color scheme
 *   (follows the system setting by default).
 * @param useDynamicColor If `false`, disables the use of dynamic theming, even
 *   when it is supported.
 * @param content The composable content the theme is applied to.
 */
@Composable
fun UiTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true,
    darkScheme: () -> ColorScheme = { darkColorScheme() },
    lightScheme: () -> ColorScheme = { lightColorScheme() },
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    UiTheme(
        colorScheme = remember(context, useDynamicColor, useDarkTheme) {
            val enableDynamicColor = useDynamicColor && supportsDynamicTheming()
            when {
                enableDynamicColor && useDarkTheme -> dynamicDarkColorScheme(context)
                enableDynamicColor -> dynamicLightColorScheme(context)
                useDarkTheme -> darkScheme()
                else -> lightScheme()
            }
        },
        shapes = shapes,
        content = content,
        typography = typography,
    )
}

/**
 * Internal theming function providing the theme inputs via [MaterialTheme].
 *
 * @param colorScheme the [ColorScheme] that will be provided by `LocalColorScheme`.
 * @param shapes the [Shapes] that will be provided by `LocalShapes`.
 * @param typography the [Typography] that will be provided by `LocalTypography`.
 * @param content The composable content the theme is applied to.
 */
@Composable
private fun UiTheme(
    colorScheme: ColorScheme,
    shapes: Shapes,
    typography: Typography,
    content: @Composable () -> Unit,
) = MaterialTheme(
    colorScheme = colorScheme,
    shapes = shapes,
    typography = typography,
    content = content,
)

/**
 * Helper function to check if the SDK supports dynamic theming.
 */
@ChecksSdkIntAtLeast(Build.VERSION_CODES.S)
private fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
