package com.alfredopaesdaluz.projectmvvmpokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alfredopaesdaluz.projectmvvmpokedex.pokemondetail.PokemonDetailScreen
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.PokemonListScreen
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonstart.StartScreen
import com.alfredopaesdaluz.projectmvvmpokedex.ui.theme.ProjectMvvmPokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectMvvmPokedexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "start_screen"
                ) {
                    composable("start_screen") {
                        StartScreen(navController = navController)
                    }
                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController)
                    }
                    composable(
                        "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor") {
                                type = NavType.IntType
                            },
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val dominantColor = remember {
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) } ?: Color.White
                        }
                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }
                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase(Locale.ROOT) ?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}