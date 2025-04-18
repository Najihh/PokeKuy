package dev.najihhome.pokekuy.data.remote.model

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonEntry>
)

data class PokemonEntry(
    val name: String,
    val url: String
) {
    fun getIdFromUrl(): Int {
        // expected URL format is: https://pokeapi.co/api/v2/pokemon/1/
        val urlWithoutEndSlash = url.removeSuffix("/")
        return urlWithoutEndSlash.substring(urlWithoutEndSlash.lastIndexOf("/") + 1).toInt()
    }
}

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val abilities: List<AbilityResponse>,
    val types: List<TypeResponse>,
    val sprites: SpritesResponse
)

data class AbilityResponse(
    val ability: NamedApiResource,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)

data class TypeResponse(
    val slot: Int,
    val type: NamedApiResource
)

data class NamedApiResource(
    val name: String,
    val url: String
)

data class SpritesResponse(
    @SerializedName("front_default")
    val frontDefault: String?,
    @SerializedName("front_shiny")
    val frontShiny: String?,
    @SerializedName("back_default")
    val backDefault: String?,
    @SerializedName("back_shiny")
    val backShiny: String?
)
