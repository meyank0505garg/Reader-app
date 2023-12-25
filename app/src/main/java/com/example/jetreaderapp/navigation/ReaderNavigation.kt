package com.example.jetreaderapp.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetreaderapp.screens.details.BookDetailsScreen
import com.example.jetreaderapp.screens.home.HomeScreen
import com.example.jetreaderapp.screens.login.ReaderLoginScreen
import com.example.jetreaderapp.screens.search.SearchScreen
import com.example.jetreaderapp.screens.splash.ReaderSplashScreen
import com.example.jetreaderapp.screens.stats.ReaderStatsScreen
import com.example.jetreaderapp.screens.update.BookUpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ReaderScreens.SplashScreen.name ){
        composable(route = ReaderScreens.SplashScreen.name){
            ReaderSplashScreen(navController = navController)
        }
        composable(route = ReaderScreens.LoginScreen.name){
            ReaderLoginScreen(navController = navController)
        }

        composable(route = ReaderScreens.ReaderHomeScreen.name){
            HomeScreen(navController = navController)
        }

        composable(route = ReaderScreens.ReaderStatsScreen.name){
            ReaderStatsScreen(navController = navController)
        }
        composable(route = ReaderScreens.SearchScreen.name){
            SearchScreen(navController = navController)
        }

        composable(
            route = ReaderScreens.DetailScreen.name + "/{bookId}",
            arguments = listOf(
                navArgument("bookId"){
                    type = NavType.StringType
                }
            )
            ){backStackEntry->
            backStackEntry.arguments?.getString("bookId").let {

                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }


        composable(
            route = ReaderScreens.UpdateScreen.name + "/{bookItemId}",
            arguments = listOf(
                navArgument("bookItemId"){
                    type = NavType.StringType
                }
            )
        ){backStackEntry->
            backStackEntry.arguments?.getString("bookItemId").let {
                Log.d("BookId", "ReaderNavigation: $it")
                BookUpdateScreen(navController,it.toString())
            }


        }

    }
}