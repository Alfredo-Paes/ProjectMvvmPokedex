package com.alfredopaesdaluz.projectmvvmpokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.alfredopaesdaluz.projectmvvmpokedex.data.remote.responses.Pokemon
import com.alfredopaesdaluz.projectmvvmpokedex.repository.PokemonRepository
import com.alfredopaesdaluz.projectmvvmpokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}