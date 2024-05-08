package org.d3if0065.assessment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.d3if0065.assessment.ui.screen.About
import org.d3if0065.assessment.ui.screen.DetailScreen
import org.d3if0065.assessment.ui.screen.FormArticle
import org.d3if0065.assessment.ui.screen.Home
import org.d3if0065.assessment.ui.screen.ID_ARTICLE
import org.d3if0065.assessment.ui.screen.Welcome

@Composable
fun Navigation(navController: NavHostController = rememberNavController(), modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = ScreenManager.Welcome.route
    ){
        composable(route = ScreenManager.Welcome.route){
            Welcome(navController)
        }
        composable(route = ScreenManager.Home.route){
            Home(navController)
        }
        composable(route = ScreenManager.About.route){
            About(navController)
        }
        composable(route = ScreenManager.FormArticle.route){
            FormArticle(navController)
        }
        composable(
            route = ScreenManager.EditArticle.route,
            arguments = listOf(
                navArgument(ID_ARTICLE) { type = NavType.LongType }
            )
        ){ navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong(ID_ARTICLE)
            FormArticle(navController, id)
        }
        composable(
            route = ScreenManager.DetailArticle.route,
            arguments = listOf(
                navArgument(ID_ARTICLE) { type = NavType.LongType }
            )
        ){ navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong(ID_ARTICLE)
            DetailScreen(navController, id)
        }

    }
}