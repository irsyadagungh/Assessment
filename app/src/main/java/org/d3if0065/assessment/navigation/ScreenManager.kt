package org.d3if0065.assessment.navigation

sealed class ScreenManager(val route: String) {
    data object Welcome: ScreenManager("welcomeScreen")
    data object About: ScreenManager("signUpScreen")
    data object Home: ScreenManager("home")
}