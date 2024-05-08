package org.d3if0065.assessment.navigation

import org.d3if0065.assessment.ui.screen.ID_ARTICLE

sealed class ScreenManager(val route: String) {
    data object Welcome: ScreenManager("welcomeScreen")
    data object About: ScreenManager("signUpScreen")
    data object Home: ScreenManager("home")
    data object FormArticle: ScreenManager("formArticle")
    data object EditArticle: ScreenManager("formArticle/{$ID_ARTICLE}"){
        fun withId(id: Long) = "formArticle/$id"
    }
    data object DetailArticle: ScreenManager("detailArticle/{$ID_ARTICLE}"){
        fun withId(id: Long) = "detailArticle/$id"
    }
}