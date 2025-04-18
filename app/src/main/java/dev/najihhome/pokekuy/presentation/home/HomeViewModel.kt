package dev.najihhome.pokekuy.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.usecase.GetPokemonListUseCase
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {
    
    private val _pokemonListState = MutableLiveData<PokemonListState>(PokemonListState())
    val pokemonListState: LiveData<PokemonListState> = _pokemonListState
    
    private val currentPokemonList = mutableListOf<Pokemon>()
    
    fun loadPokemonList(offset: Int, limit: Int) {
        if (offset == 0) {
            _pokemonListState.value = PokemonListState(isLoading = true)
        }
        
        viewModelScope.launch {
            val result = getPokemonListUseCase(offset, limit)
            
            result.fold(
                onSuccess = { pokemonList ->
                    // Add the new items to the current list
                    if (offset == 0) {
                        currentPokemonList.clear()
                    }
                    currentPokemonList.addAll(pokemonList)
                    
                    _pokemonListState.value = PokemonListState(
                        pokemonList = currentPokemonList.toList()
                    )
                },
                onFailure = { exception ->
                    _pokemonListState.value = PokemonListState(
                        error = exception.message ?: "Failed to load Pokemon list"
                    )
                }
            )
        }
    }

    data class PokemonListState(
        val isLoading: Boolean = false,
        val pokemonList: List<Pokemon> = emptyList(),
        val error: String? = null
    )
}
