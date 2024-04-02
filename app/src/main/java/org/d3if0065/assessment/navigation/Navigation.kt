package org.d3if0065.assessment.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.d3if0065.assessment.ui.screen.About
import org.d3if0065.assessment.ui.screen.Home
import org.d3if0065.assessment.ui.screen.Welcome
import org.d3if0065.assessment.viewModel.ArticleViewModel

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
    }
}