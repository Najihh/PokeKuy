package dev.najihhome.pokekuy.domain.model

data class Pokemon(
    val id: Int,
    val name: String,
    val url: String,
    val abilities: List<PokemonAbility>
) {
    fun getFormattedName(): String {
        return name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}

data class PokemonAbility(
    val name: String,
    val url: String,
    val isHidden: Boolean,
    val slot: Int
) {
    fun getFormattedName(): String {
        return name.replace("-", " ")
            .split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            }
    }
}
