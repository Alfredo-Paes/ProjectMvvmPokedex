package com.alfredopaesdaluz.projectmvvmpokedex.repository

import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.PokeApi
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Pokemon
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.PokemonList
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch(e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}