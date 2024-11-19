import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Pokemon
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Species
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Sprites
import com.alfredopaesdaluz.projectmvvmpokedex.pokemondetail.PokemonDetailViewModel
import com.alfredopaesdaluz.projectmvvmpokedex.repository.PokemonRepository
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<PokemonRepository>()
    private lateinit var viewModel: PokemonDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PokemonDetailViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPokemonInfo returns success`() = runTest(testDispatcher) {
        // Arrange
        val fakePokemon = Pokemon(
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
        coEvery { mockRepository.getPokemonInfo("pikachu") } returns Resource.Success(fakePokemon)

        // Act
        val result = viewModel.getPokemonInfo("pikachu")

        // Assert
        assert(result is Resource.Success)
        assertEquals(fakePokemon, (result as Resource.Success).data)
    }

    @Test
    fun `getPokemonInfo returns error`() = runTest(testDispatcher) {
        // Arrange
        val errorMessage = "Pokemon not found"
        coEvery { mockRepository.getPokemonInfo("unknown") } returns Resource.Error(errorMessage)

        // Act
        val result = viewModel.getPokemonInfo("unknown")

        // Assert
        assert(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)
    }
}
