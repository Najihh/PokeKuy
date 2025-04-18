package dev.najihhome.pokekuy.domain.repository

import dev.najihhome.pokekuy.domain.model.Pokemon

interface PokemonRepository {
    suspend fun getPokemonList(offset: Int, limit: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetail(nameOrId: String): Result<Pokemon>
    suspend fun searchPokemon(query: String): Result<List<Pokemon>>
}
