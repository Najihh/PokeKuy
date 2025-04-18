package dev.najihhome.pokekuy.data.remote

import dev.najihhome.pokekuy.data.remote.model.PokemonDetailResponse
import dev.najihhome.pokekuy.data.remote.model.PokemonListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApi {
    
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10
    ): Response<PokemonListResponse>

    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(
        @Path("nameOrId") nameOrId: String
    ): Response<PokemonDetailResponse>
}
