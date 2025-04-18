package dev.najihhome.pokekuy.domain.usecase

import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPokemonDetailUseCase(private val pokemonRepository: PokemonRepository) {
    
    suspend operator fun invoke(nameOrId: String): Result<Pokemon> = withContext(Dispatchers.IO) {
        if (nameOrId.isBlank()) {
            return@withContext Result.failure(Exception("Pokemon name or ID cannot be empty"))
        }
        
        return@withContext pokemonRepository.getPokemonDetail(nameOrId)
    }
}
