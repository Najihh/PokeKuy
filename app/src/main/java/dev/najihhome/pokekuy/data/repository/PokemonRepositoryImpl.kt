package dev.najihhome.pokekuy.data.repository

import android.util.Log
import dev.najihhome.pokekuy.common.NetworkUtil
import dev.najihhome.pokekuy.data.local.PokemonDatabase
import dev.najihhome.pokekuy.data.remote.PokemonApi
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.model.PokemonAbility
import dev.najihhome.pokekuy.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonRepositoryImpl(
    private val pokemonApi: PokemonApi,
    private val pokemonDatabase: PokemonDatabase,
    private val networkUtil: NetworkUtil
) : PokemonRepository {
    
    override suspend fun getPokemonList(offset: Int, limit: Int): Result<List<Pokemon>> = withContext(Dispatchers.IO) {
        try {
            // check connection
            if (networkUtil.isNetworkAvailable()) {
                try {
                    val response = pokemonApi.getPokemonList(offset, limit)
                    
                    if (response.isSuccessful) {
                        val pokemonList = response.body()?.results?.map { pokemonEntry ->
                            val id = pokemonEntry.getIdFromUrl()
                            Pokemon(
                                id = id,
                                name = pokemonEntry.name,
                                url = pokemonEntry.url,
                                abilities = emptyList()
                            )
                        } ?: emptyList()
                        
                        // save to local database
                        pokemonDatabase.savePokemonList(pokemonList)
                        
                        return@withContext Result.success(pokemonList)
                    }
                } catch (e: Exception) {
                    Log.e("PokemonRepository", "Network request failed: ${e.message}, looking for local database")
                }
            }

            val localPokemonList = pokemonDatabase.getPokemonList(offset, limit)
            if (localPokemonList.isNotEmpty()) {
                return@withContext Result.success(localPokemonList)
            } else {
                return@withContext Result.failure(Exception("Failed to get Pokemon data"))
            }
            
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("Failed to get Pokemon data: ${e.message}"))
        }
    }

    override suspend fun getPokemonDetail(nameOrId: String): Result<Pokemon> = withContext(Dispatchers.IO) {
        try {
            if (networkUtil.isNetworkAvailable()) {
                try {
                    val response = pokemonApi.getPokemonDetail(nameOrId)
                    
                    if (response.isSuccessful) {
                        val pokemonDetail = response.body()
                        
                        if (pokemonDetail != null) {
                            val abilities = pokemonDetail.abilities.map { abilityResponse ->
                                PokemonAbility(
                                    name = abilityResponse.ability.name,
                                    url = abilityResponse.ability.url,
                                    isHidden = abilityResponse.isHidden,
                                    slot = abilityResponse.slot
                                )
                            }
                            
                            val pokemon = Pokemon(
                                id = pokemonDetail.id,
                                name = pokemonDetail.name,
                                url = "https://pokeapi.co/api/v2/pokemon/${pokemonDetail.id}/",
                                abilities = abilities
                            )

                            pokemonDatabase.savePokemonDetail(pokemon)
                            
                            return@withContext Result.success(pokemon)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PokemonRepository", "Network request failed: ${e.message}, looking for local database")
                }
            }

            val localPokemon = if (nameOrId.toIntOrNull() != null) {
                pokemonDatabase.getPokemonDetail(nameOrId.toInt())
            } else {
                pokemonDatabase.getPokemonByName(nameOrId)
            }
            
            if (localPokemon != null) {
                return@withContext Result.success(localPokemon)
            } else {
                return@withContext Result.failure(Exception("Failed to get Pokemon details"))
            }
            
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("Failed to get Pokemon details: ${e.message}"))
        }
    }

    override suspend fun searchPokemon(query: String): Result<List<Pokemon>> = withContext(Dispatchers.IO) {
        try {
            // Optimize search with local database first
            val localResults = pokemonDatabase.searchPokemon(query)
            if (localResults.isNotEmpty()) {
                return@withContext Result.success(localResults)
            }

            // i didn't found the search endpoint, so im just playing with logic with pokemon detail endpoint
            if (networkUtil.isNetworkAvailable()) {
                try {
                    val response = pokemonApi.getPokemonDetail(query.lowercase())
                    if (response.isSuccessful) {
                        val pokemonDetail = response.body()
                        
                        if (pokemonDetail != null) {
                            val abilities = pokemonDetail.abilities.map { abilityResponse ->
                                PokemonAbility(
                                    name = abilityResponse.ability.name,
                                    url = abilityResponse.ability.url,
                                    isHidden = abilityResponse.isHidden,
                                    slot = abilityResponse.slot
                                )
                            }
                            
                            val pokemon = Pokemon(
                                id = pokemonDetail.id,
                                name = pokemonDetail.name,
                                url = "https://pokeapi.co/api/v2/pokemon/${pokemonDetail.id}/",
                                abilities = abilities
                            )
                            pokemonDatabase.savePokemonDetail(pokemon)
                            return@withContext Result.success(listOf(pokemon))
                        }
                    }
                } catch (e: Exception) {
                    // API fail because no pokemon found, just return empty list
                    return@withContext Result.success(emptyList())
                }
            }
            return@withContext Result.success(emptyList())
            
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("Failed to search Pokemon: ${e.message}"))
        }
    }
}
