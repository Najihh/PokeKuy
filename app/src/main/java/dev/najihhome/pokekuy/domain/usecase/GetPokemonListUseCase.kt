package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPokemonListUseCase(private val pokemonRepository: PokemonRepository) {
    
    suspend operator fun invoke(offset: Int, limit: Int = 10): Result<List<Pokemon>> = withContext(Dispatchers.IO) {
        if (offset < 0) {
            return@withContext Result.failure(Exception("Offset cannot be negative"))
        }
        
        if (limit <= 0) {
            return@withContext Result.failure(Exception("Limit must be positive"))
        }
        
        return@withContext pokemonRepository.getPokemonList(offset, limit)
    }
}
