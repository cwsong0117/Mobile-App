package com.hermen.ass1.ui.theme

sealed class Screen(val route: String) {
    object InitialPage : Screen("initialPage")
    object Signup : Screen("signup")
    object Login : Screen("login")
}