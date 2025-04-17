package com.hermen.ass1.ui.theme

sealed class Screen(val route: String) {
    object InitialPage : Screen("initial_page")
    object Signup : Screen("signup")
    object Login : Screen("login")
    object Main : Screen("main")
}
