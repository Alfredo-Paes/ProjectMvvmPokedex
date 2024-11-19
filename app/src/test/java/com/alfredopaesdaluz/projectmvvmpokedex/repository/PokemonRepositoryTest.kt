package com.alfredopaesdaluz.projectmvvmpokedex.repository

import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.PokeApi
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Pokemon
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.PokemonList
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Result
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Species
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Sprites
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PokemonRepositoryTest {

    private val mockApi = mockk<PokeApi>()
    private val repository = PokemonRepository(mockApi)

    @Test
    fun `getPokemonList returns success when API call is successful`() = runBlocking {
        // Arrange
        val fakePokemonList = PokemonList(
            count = 2,
            next = "nextUrl",
            previous = null,
            results = listOf(
                Result("pikachu","url1"),
                Result("bulbasaur","url2")
            )
        )
        coEvery { mockApi.getPokemonList(any(), any()) } returns fakePokemonList

        // Act
        val result = repository.getPokemonList(10, 0)

        // Assert
        assert(result is Resource.Success) { "Expected Resource.Success but got $result" }
        assertEquals(fakePokemonList, (result as Resource.Success).data)
    }

    @Test
    fun `getPokemonList returns error when API call throws exception`() = runBlocking {
        // Arrange
        val errorMessage = "An unknown error occured."
        coEvery { mockApi.getPokemonList(any(), any()) } throws Exception()

        // Act
        val result = repository.getPokemonList(10, 0)

        // Assert
        assert(result is Resource.Error) { "Expected Resource.Error but got $result" }
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

    @Test
    fun `getPokemonInfo returns success when API call is successful`() = runBlocking {
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
        coEvery { mockApi.getPokemonInfo("pikachu") } returns fakePokemon

        // Act
        val result = repository.getPokemonInfo("pikachu")

        // Assert
        assert(result is Resource.Success) { "Expected Resource.Success but got $result" }
        assertEquals(fakePokemon, (result as Resource.Success).data)
    }

    @Test
    fun `getPokemonInfo returns error when API call throws exception`() = runBlocking {
        // Arrange
        val errorMessage = "An unknown error occured."
        coEvery { mockApi.getPokemonInfo(any()) } throws Exception()

        // Act
        val result = repository.getPokemonInfo("pikachu")

        // Assert
        assert(result is Resource.Error) { "Expected Resource.Error but got $result" }
        assertEquals(errorMessage, (result as Resource.Error).message)
    }

}
