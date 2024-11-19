package com.alfredopaesdaluz.projectmvvmpokedex.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavController
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Pokemon
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Species
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Sprites
import com.alfredopaesdaluz.projectmvvmpokedex.pokemondetail.PokemonDetailScreen
import com.alfredopaesdaluz.projectmvvmpokedex.pokemondetail.PokemonDetailViewModel
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PokemonDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockNavController = mockk<NavController>(relaxed = true)
    private val mockViewModel = mockk<PokemonDetailViewModel>(relaxed = true)
    private val mockPokemon = Pokemon(
        abilities = emptyList(),
        baseExperience = 112,
        forms = emptyList(),
        gameIndices = emptyList(),
        height = 4,
        heldItems = emptyList(),
        id = 1,
        isDefault = true,
        locationAreaEncounters = "",
        moves = emptyList(),
        name = "pikachu",
        order = 1,
        pastTypes = emptyList(),
        species = Species("pikachu", "url"),
        sprites = Sprites(
            backFemale = null,
            backShiny = null.toString(),
            backShinyFemale = null,
            frontDefault = "url",
            frontFemale = null,
            frontShiny = null.toString(),
            frontShinyFemale = null,
            other = null,
            versions = null,
            backDefault = null.toString()
        ),
        stats = emptyList(),
        types = emptyList(),
        weight = 60
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

        // Configuração de estados do ViewModel com funções suspensas
        coEvery { mockViewModel.getPokemonInfo("pikachu") } returns Resource.Success(mockPokemon)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun pokemonDetailScreen_displaysPokemonData_onSuccess() {
        composeTestRule.setContent {
            PokemonDetailScreen(
                dominantColor = Color.Yellow,
                pokemonName = "pikachu",
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        // Verifica detalhes do Pokémon
        composeTestRule
            .onNodeWithText("#25 Pikachu")
            .assertExists()

        composeTestRule
            .onNodeWithText("electric")
            .assertExists()

        composeTestRule
            .onNodeWithText("60kg")
            .assertExists()

        composeTestRule
            .onNodeWithText("0.4m")
            .assertExists()
    }

    @Test
    fun pokemonDetailScreen_displaysLoadingIndicator_whenLoading() {
        coEvery { mockViewModel.getPokemonInfo("pikachu") } returns Resource.Loading()

        composeTestRule.setContent {
            PokemonDetailScreen(
                dominantColor = Color.Yellow,
                pokemonName = "pikachu",
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Loading Indicator")
            .assertExists()
    }

    @Test
    fun pokemonDetailScreen_displaysErrorMessage_onError() {
        coEvery { mockViewModel.getPokemonInfo("pikachu") } returns Resource.Error("Network Error")

        composeTestRule.setContent {
            PokemonDetailScreen(
                dominantColor = Color.Yellow,
                pokemonName = "pikachu",
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithText("Network Error")
            .assertExists()
    }

    @Test
    fun pokemonDetailScreen_backButtonNavigatesCorrectly() {
        composeTestRule.setContent {
            PokemonDetailScreen(
                dominantColor = Color.Yellow,
                pokemonName = "pikachu",
                navController = mockNavController,
                viewModel = mockViewModel
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back Button")
            .performClick()

        verify { mockNavController.popBackStack() }
    }
}

annotation class HiltAndroidTest

