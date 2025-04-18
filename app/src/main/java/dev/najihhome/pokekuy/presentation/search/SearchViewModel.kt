package dev.najihhome.pokekuy.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.usecase.SearchPokemonUseCase
import kotlinx.coroutines.launch

class SearchViewModel(
    private val query: String,
    private val searchPokemonUseCase: SearchPokemonUseCase
) : ViewModel() {
    
    private val _searchState = MutableLiveData<SearchState>(SearchState())
    val searchState: LiveData<SearchState> = _searchState
    
    fun searchPokemon() {
        _searchState.value = SearchState(isLoading = true)
        
        viewModelScope.launch {
            val result = searchPokemonUseCase(query)
            
            result.fold(
                onSuccess = { pokemonList ->
                    _searchState.value = SearchState(pokemonList = pokemonList)
                },
                onFailure = { exception ->
                    _searchState.value = SearchState(error = exception.message ?: "Failed to search Pokemon")
                }
            )
        }
    }
    
    data class SearchState(
        val isLoading: Boolean = false,
        val pokemonList: List<Pokemon> = emptyList(),
        val error: String? = null
    )
}
