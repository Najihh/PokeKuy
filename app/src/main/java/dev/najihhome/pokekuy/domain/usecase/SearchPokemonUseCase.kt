package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchPokemonUseCase(private val pokemonRepository: PokemonRepository) {
    
    suspend operator fun invoke(query: String): Result<List<Pokemon>> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            return@withContext Result.failure(Exception("Search query cannot be empty"))
        }
        
        return@withContext pokemonRepository.searchPokemon(query)
    }
}
