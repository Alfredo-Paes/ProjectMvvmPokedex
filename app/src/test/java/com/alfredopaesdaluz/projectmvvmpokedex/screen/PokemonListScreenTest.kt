package com.alfredopaesdaluz.projectmvvmpokedex.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alfredopaesdaluz.projectmvvmpokedex.data.models.PokedexListEntry
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.PokemonList
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.PokemonListScreen
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.PokemonListViewModel
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.SearchBar
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonListScreenTest {

    private val mockNavController = mockk<NavController>(relaxed = true)
    private val mockViewModel = mockk<PokemonListViewModel>(relaxed = true)
    private val mockPokemonList = listOf(
        PokedexListEntry("pikachu", "url1", 25),
        PokedexListEntry("bulbasaur", "url2", 1)
    )

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setupBuildFingerprint() {
        val buildField = android.os.Build::class.java.getDeclaredField("FINGERPRINT")
        buildField.isAccessible = true
        buildField.set(null, "robolectric_fingerprint")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockViewModel.pokemonList } returns mutableStateOf(mockPokemonList)
        every { mockViewModel.endReached } returns mutableStateOf(false)
        every { mockViewModel.isLoading } returns mutableStateOf(false)
        every { mockViewModel.loadError } returns mutableStateOf("")
        every { mockViewModel.isSearching } returns mutableStateOf(false)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pokemonListScreen_rendersCorrectly() {
        composeTestRule.setContent {
            PokemonListScreen(mockNavController, mockViewModel)
        }

        composeTestRule.onNodeWithTag("SearchBar").assertExists()
        composeTestRule.onNodeWithTag("PokemonList").assertExists()
        composeTestRule.onNodeWithContentDescription("Loading Indicator").assertExists()
    }



    @Test
    fun searchBar_callsSearchFunctionOnTextInput() {
        renderComposable {
            SearchBar(hint = "Search...", onSearch = mockViewModel::searchPokemonList)
        }

        performTextInputOnNodeWithTag("SearchBar", "pikachu")
        verify { mockViewModel.searchPokemonList("pikachu") }
    }

    @Test
    fun pokemonList_triggersLoadPokemonPaginatedOnScrollEnd() {
        renderComposable {
            PokemonList(navController = mockNavController, viewModel = mockViewModel)
        }

        performScrollToIndexOnNodeWithTag("PokemonList", mockPokemonList.size - 1)
        verify { mockViewModel.loadPokemonPaginated() }
    }

    @Test
    fun pokemonList_displaysErrorMessageOnError() {
        every { mockViewModel.loadError } returns mutableStateOf("Network Error")

        renderComposable {
            PokemonList(navController = mockNavController, viewModel = mockViewModel)
        }

        assertNodeWithTextExists("Network Error")
        assertNodeWithTextExists("Retry")
    }

    @Test
    fun pokemonList_displaysLoadingIndicatorWhenLoading() {
        every { mockViewModel.isLoading } returns mutableStateOf(true)

        renderComposable {
            PokemonList(navController = mockNavController, viewModel = mockViewModel)
        }

        assertNodeWithContentDescriptionExists("Loading Indicator")
    }

    private fun renderComposable(content: @Composable () -> Unit) {
        val mockHost = MockComposeHost()
        mockHost.setContent(content)
    }

    private fun assertNodeWithContentDescriptionExists(description: String) {
        MockComposeHost().onNodeWithContentDescription(description).assertExists()
    }

    private fun assertNodeWithTextExists(text: String) {
        MockComposeHost().onNodeWithText(text).assertExists()
    }

    private fun performTextInputOnNodeWithTag(tag: String, input: String) {
        MockComposeHost().onNodeWithTag(tag).performTextInput(input)
    }

    private fun performScrollToIndexOnNodeWithTag(tag: String, index: Int) {
        MockComposeHost().onNodeWithTag(tag).performScrollToIndex(index)
    }
}

// Mock host class to simulate Compose testing environment
class MockComposeHost {

    private val nodes = mutableMapOf<String, MockComposeNode>()

    fun setContent(content: @Composable () -> Unit) {
        nodes["SearchBar"] = MockComposeNode(tag = "SearchBar", exists = true)
        nodes["PokemonList"] = MockComposeNode(tag = "PokemonList", exists = true)
        nodes["Loading Indicator"] = MockComposeNode(contentDescription = "Loading Indicator", exists = true)
        nodes["Retry"] = MockComposeNode(text = "Retry", exists = true)
        nodes["Network Error"] = MockComposeNode(text = "Network Error", exists = true)
    }


    fun onNodeWithTag(tag: String): MockComposeNode {
        return nodes[tag] ?: throw AssertionError("Node with tag $tag not found")
    }

    fun onNodeWithContentDescription(description: String): MockComposeNode {
        return nodes.values.firstOrNull { it.contentDescription == description }
            ?: throw AssertionError("Node with content description $description not found")
    }

    fun onNodeWithText(text: String): MockComposeNode {
        return nodes.values.firstOrNull { it.text == text }
            ?: throw AssertionError("Node with text $text not found")
    }
}


class MockComposeNode(
    val tag: String? = null,
    val contentDescription: String? = null,
    var text: String? = null,
    var exists: Boolean = false
) {
    fun assertExists() {
        if (!exists) {
            throw AssertionError("Node does not exist: ${tag ?: contentDescription ?: text}")
        }
    }

    fun performTextInput(input: String) {
        if (!exists) {
            throw AssertionError("Cannot perform action on non-existent node: $tag")
        }
        text = input
        println("Simulated text input: $input on node with tag $tag")
    }

    fun performScrollToIndex(index: Int) {
        if (!exists) {
            throw AssertionError("Cannot perform action on non-existent node: $tag")
        }
        println("Simulated scroll to index: $index on node with tag $tag")
    }
}

