package dev.najihhome.pokekuy.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import dev.najihhome.pokekuy.domain.model.Pokemon
import dev.najihhome.pokekuy.domain.model.PokemonAbility

class PokemonDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "pokekuy_db.db"
        private const val DATABASE_VERSION = 1

        // Pokemon table
        private const val TABLE_POKEMON = "pokemon"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_URL = "url"
        private const val COLUMN_CACHED_AT = "cached_at"

        // Pokemon abilities table
        private const val TABLE_ABILITIES = "abilities"
        private const val COLUMN_ABILITY_ID = "id"
        private const val COLUMN_POKEMON_ID = "pokemon_id"
        private const val COLUMN_ABILITY_NAME = "ability_name"
        private const val COLUMN_ABILITY_URL = "ability_url"
        private const val COLUMN_IS_HIDDEN = "is_hidden"
        private const val COLUMN_SLOT = "slot"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createPokemonTableQuery = """
            CREATE TABLE $TABLE_POKEMON (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_URL TEXT NOT NULL,
                $COLUMN_CACHED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        val createAbilitiesTableQuery = """
            CREATE TABLE $TABLE_ABILITIES (
                $COLUMN_ABILITY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_POKEMON_ID INTEGER NOT NULL,
                $COLUMN_ABILITY_NAME TEXT NOT NULL,
                $COLUMN_ABILITY_URL TEXT NOT NULL,
                $COLUMN_IS_HIDDEN INTEGER NOT NULL,
                $COLUMN_SLOT INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_POKEMON_ID) REFERENCES $TABLE_POKEMON($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createPokemonTableQuery)
        db.execSQL(createAbilitiesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ABILITIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POKEMON")
        onCreate(db)
    }

    fun savePokemonList(pokemonList: List<Pokemon>) {
        val db = writableDatabase
        db.beginTransaction()

        try {
            for (pokemon in pokemonList) {
                val contentValues = ContentValues().apply {
                    put(COLUMN_ID, pokemon.id)
                    put(COLUMN_NAME, pokemon.name)
                    put(COLUMN_URL, pokemon.url)
                    put(COLUMN_CACHED_AT, System.currentTimeMillis())
                }

                db.insertWithOnConflict(
                    TABLE_POKEMON,
                    null,
                    contentValues,
                    SQLiteDatabase.CONFLICT_REPLACE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getPokemonList(offset: Int, limit: Int): List<Pokemon> {
        val pokemonList = mutableListOf<Pokemon>()
        val db = readableDatabase

        val query = "SELECT * FROM $TABLE_POKEMON ORDER BY $COLUMN_ID LIMIT $limit OFFSET $offset"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))

                pokemonList.add(Pokemon(id, name, url, emptyList()))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pokemonList
    }

    fun savePokemonDetail(pokemon: Pokemon) {
        val db = writableDatabase
        db.beginTransaction()

        try {
            // Save or update Pokemon
            val pokemonValues = ContentValues().apply {
                put(COLUMN_ID, pokemon.id)
                put(COLUMN_NAME, pokemon.name)
                put(COLUMN_URL, pokemon.url)
                put(COLUMN_CACHED_AT, System.currentTimeMillis())
            }

            db.insertWithOnConflict(
                TABLE_POKEMON,
                null,
                pokemonValues,
                SQLiteDatabase.CONFLICT_REPLACE
            )

            // Delete existing abilities for this Pokemon
            db.delete(
                TABLE_ABILITIES,
                "$COLUMN_POKEMON_ID = ?",
                arrayOf(pokemon.id.toString())
            )

            // Insert new abilities
            for (ability in pokemon.abilities) {
                val abilityValues = ContentValues().apply {
                    put(COLUMN_POKEMON_ID, pokemon.id)
                    put(COLUMN_ABILITY_NAME, ability.name)
                    put(COLUMN_ABILITY_URL, ability.url)
                    put(COLUMN_IS_HIDDEN, if (ability.isHidden) 1 else 0)
                    put(COLUMN_SLOT, ability.slot)
                }

                db.insert(TABLE_ABILITIES, null, abilityValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getPokemonDetail(id: Int): Pokemon? {
        val db = readableDatabase

        // Get Pokemon data
        val pokemonQuery = "SELECT * FROM $TABLE_POKEMON WHERE $COLUMN_ID = ?"
        val pokemonCursor = db.rawQuery(pokemonQuery, arrayOf(id.toString()))

        var pokemon: Pokemon? = null

        if (pokemonCursor.moveToFirst()) {
            val pokemonId = pokemonCursor.getInt(pokemonCursor.getColumnIndexOrThrow(COLUMN_ID))
            val name = pokemonCursor.getString(pokemonCursor.getColumnIndexOrThrow(COLUMN_NAME))
            val url = pokemonCursor.getString(pokemonCursor.getColumnIndexOrThrow(COLUMN_URL))
            val abilities = getPokemonAbilities(db, pokemonId)

            pokemon = Pokemon(pokemonId, name, url, abilities)
        }

        pokemonCursor.close()
        db.close()
        return pokemon
    }

    fun searchPokemon(query: String): List<Pokemon> {
        val pokemonList = mutableListOf<Pokemon>()
        val db = readableDatabase

        val searchQuery = "SELECT * FROM $TABLE_POKEMON WHERE $COLUMN_NAME LIKE ?"
        val cursor = db.rawQuery(searchQuery, arrayOf("%$query%"))

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL))

                pokemonList.add(Pokemon(id, name, url, emptyList()))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return pokemonList
    }

    fun getPokemonByName(name: String): Pokemon? {
        val db = readableDatabase

        val pokemonQuery = "SELECT * FROM $TABLE_POKEMON WHERE $COLUMN_NAME = ? COLLATE NOCASE"
        val pokemonCursor = db.rawQuery(pokemonQuery, arrayOf(name))

        var pokemon: Pokemon? = null

        if (pokemonCursor.moveToFirst()) {
            val pokemonId = pokemonCursor.getInt(pokemonCursor.getColumnIndexOrThrow(COLUMN_ID))
            val pokemonName =
                pokemonCursor.getString(pokemonCursor.getColumnIndexOrThrow(COLUMN_NAME))
            val url = pokemonCursor.getString(pokemonCursor.getColumnIndexOrThrow(COLUMN_URL))
            val abilities = getPokemonAbilities(db, pokemonId)

            pokemon = Pokemon(pokemonId, pokemonName, url, abilities)
        }

        pokemonCursor.close()
        db.close()
        return pokemon
    }

    private fun getPokemonAbilities(db: SQLiteDatabase, pokemonId: Int): List<PokemonAbility> {
        val abilities = mutableListOf<PokemonAbility>()

        val abilityQuery = "SELECT * FROM $TABLE_ABILITIES WHERE $COLUMN_POKEMON_ID = ?"
        val abilityCursor = db.rawQuery(abilityQuery, arrayOf(pokemonId.toString()))

        if (abilityCursor.moveToFirst()) {
            do {
                val name =
                    abilityCursor.getString(abilityCursor.getColumnIndexOrThrow(COLUMN_ABILITY_NAME))
                val url =
                    abilityCursor.getString(abilityCursor.getColumnIndexOrThrow(COLUMN_ABILITY_URL))
                val isHidden =
                    abilityCursor.getInt(abilityCursor.getColumnIndexOrThrow(COLUMN_IS_HIDDEN)) == 1
                val slot = abilityCursor.getInt(abilityCursor.getColumnIndexOrThrow(COLUMN_SLOT))

                abilities.add(PokemonAbility(name, url, isHidden, slot))
            } while (abilityCursor.moveToNext())
        }

        abilityCursor.close()
        return abilities
    }
}
