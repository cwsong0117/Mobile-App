package com.hermen.ass1

import android.content.Context
import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.hermen.ass1.ui.theme.utils.NavigationType


fun getNavigationType(
    context: Context,
    orientation: Int
): NavigationType {
    val isTablet = isTablet(context)

    return when {
        !isTablet && orientation == Configuration.ORIENTATION_PORTRAIT ->
            NavigationType.BOTTOM_NAVIGATION

        !isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE ->
            NavigationType.NAVIGATION_RAIL

        isTablet && orientation == Configuration.ORIENTATION_PORTRAIT ->
            NavigationType.NAVIGATION_RAIL

        isTablet && orientation == Configuration.ORIENTATION_LANDSCAPE ->
            NavigationType.PERMANENT_NAVIGATION_DRAWER

        else -> NavigationType.BOTTOM_NAVIGATION
    }
}

fun isTablet(context: Context): Boolean {
    return context.resources.configuration.smallestScreenWidthDp >= 600
}