package com.alfredopaesdaluz.projectmvvmpokedex.viewModel

import com.alfredopaesdaluz.projectmvvmpokedex.data.models.PokedexListEntry
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.PokemonList
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Result
import com.alfredopaesdaluz.projectmvvmpokedex.pokemonlist.PokemonListViewModel
import com.alfredopaesdaluz.projectmvvmpokedex.repository.PokemonRepository
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = mockk<PokemonRepository>(relaxed = true)
    private lateinit var viewModel: PokemonListViewModel
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        testScope = TestScope(testDispatcher)
        viewModel = PokemonListViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls loadPokemonPaginated`() = testScope.runTest {
        // Arrange
        coEvery { mockRepository.getPokemonList(any(), any()) } returns Resource.Success(
            PokemonList(
                0,
                null.toString(),
                null,
                emptyList()
            )
        )

        // Act
        // ViewModel init should automatically trigger loadPokemonPaginated

        // Assert
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(0, viewModel.pokemonList.value.size)
    }

    @Test
    fun `loadPokemonPaginated updates states on success`() = testScope.runTest {
        // Arrange
        val fakePokemonList = PokemonList(
            2,
            null.toString(),
            null,
            listOf(
                Result("pikachu", "url1"),
                Result("bulbasaur", "url2")
            )
        )
        coEvery { mockRepository.getPokemonList(any(), any()) } returns Resource.Success(fakePokemonList)

        // Act
        viewModel.loadPokemonPaginated()

        // Assert
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(2, viewModel.pokemonList.value.size)
        assertEquals(true, viewModel.endReached.value)
    }

    @Test
    fun `loadPokemonPaginated updates loadError on failure`() = testScope.runTest {
        // Arrange
        val errorMessage = "An unknown error occurred."
        coEvery { mockRepository.getPokemonList(any(), any()) } returns Resource.Error(errorMessage)

        // Act
        viewModel.loadPokemonPaginated()

        // Assert
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(errorMessage, viewModel.loadError.value)
    }

    @Test
    fun `searchPokemonList filters correctly by name`() = runTest(testDispatcher) {
        // Arrange
        val initialList = listOf(
            PokedexListEntry("pikachu", "url1", 25),
            PokedexListEntry("bulbasaur", "url2", 1)
        )
        viewModel.pokemonList.value = initialList
        viewModel.cachedPokemonList = initialList

        // Act
        viewModel.searchPokemonList("pikachu")
        advanceUntilIdle() // Aguarda a execução de todas as corrotinas

        // Assert
        assertEquals(1, viewModel.pokemonList.value.size)
        assertEquals("pikachu", viewModel.pokemonList.value[0].pokemonName)
    }


    @Test
    fun `searchPokemonList resets to cached list on empty query`() = testScope.runTest {
        // Arrange
        val cachedList = listOf(
            PokedexListEntry("pikachu", "url1", 25),
            PokedexListEntry("bulbasaur", "url2", 1)
        )
        viewModel.cachedPokemonList = cachedList
        viewModel.pokemonList.value = listOf(PokedexListEntry("charizard", "url3", 6))

        // Act
        viewModel.searchPokemonList("")

        // Assert
        assertEquals(cachedList.size, viewModel.pokemonList.value.size)
        assertEquals(cachedList, viewModel.pokemonList.value)
    }
}
