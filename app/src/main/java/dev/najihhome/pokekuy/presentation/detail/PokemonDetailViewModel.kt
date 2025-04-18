package dev.najihhome.pokekuy.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.usecase.GetPokemonDetailUseCase
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val pokemonId: String,
    private val getPokemonDetailUseCase: GetPokemonDetailUseCase
) : ViewModel() {
    
    private val _detailState = MutableLiveData<DetailState>(DetailState())
    val detailState: LiveData<DetailState> = _detailState
    
    fun loadPokemonDetail() {
        _detailState.value = DetailState(isLoading = true)
        
        viewModelScope.launch {
            val result = getPokemonDetailUseCase(pokemonId)
            
            result.fold(
                onSuccess = { pokemon ->
                    _detailState.value = DetailState(pokemon = pokemon)
                },
                onFailure = { exception ->
                    _detailState.value = DetailState(error = exception.message ?: "Failed to load Pokemon details")
                }
            )
        }
    }
    
    data class DetailState(
        val isLoading: Boolean = false,
        val pokemon: Pokemon? = null,
        val error: String? = null
    )
}
