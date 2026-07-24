package com.example.mymovies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mymovies.feature.detail.presentation.DetailScreen
import com.example.mymovies.feature.home.presentation.HomeScreen
import com.example.mymovies.feature.home.presentation.discover.DiscoverScreen
import com.example.mymovies.feature.home.presentation.genre.GenreListScreen
import com.example.mymovies.ui.theme.MyMoviesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMoviesTheme {
                val navController = rememberNavController()
                
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(
                            viewModel = hiltViewModel(),
                            onNavigateToDetail = { movieId ->
                                navController.navigate("detail/$movieId")
                            },
                            onNavigateToGenreList = {
                                navController.navigate("genres")
                            },
                            onNavigateToDiscover = { genreId, genreName ->
                                navController.navigate("discover/$genreId/$genreName")
                            }
                        )
                    }
                    composable(
                        route = "detail/{movieId}",
                        arguments = listOf(navArgument("movieId") { type = NavType.StringType })
                    ) {
                        DetailScreen(
                            viewModel = hiltViewModel(),
                            onNavigateToDetail = { movieId ->
                                navController.navigate("detail/$movieId") {
                                    launchSingleTop = true
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("genres") {
                        GenreListScreen(
                            viewModel = hiltViewModel(),
                            onNavigateToDiscover = { genreId, genreName ->
                                navController.navigate("discover/$genreId/$genreName")
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable(
                        route = "discover/{genreId}/{genreName}",
                        arguments = listOf(
                            navArgument("genreId") { type = NavType.IntType },
                            navArgument("genreName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
                        val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
                        DiscoverScreen(
                            genreId = genreId,
                            genreName = genreName,
                            viewModel = hiltViewModel(),
                            onNavigateToDetail = { movieId ->
                                navController.navigate("detail/$movieId")
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
