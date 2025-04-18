package dev.najihhome.pokekuy.di

import dev.najihhome.pokekuy.common.NetworkUtil
import dev.najihhome.pokekuy.data.local.PokemonDatabase
import dev.najihhome.pokekuy.data.local.SessionManager
import dev.najihhome.pokekuy.data.local.UserDatabase
import dev.najihhome.pokekuy.data.remote.PokemonApi
import dev.najihhome.pokekuy.data.repository.PokemonRepositoryImpl
import dev.najihhome.pokekuy.data.repository.UserRepositoryImpl
import dev.najihhome.pokekuy.domain.repository.PokemonRepository
import dev.najihhome.pokekuy.domain.repository.UserRepository
import dev.najihhome.pokekuy.domain.usecase.GetCurrentUserUseCase
import dev.najihhome.pokekuy.domain.usecase.GetPokemonDetailUseCase
import dev.najihhome.pokekuy.domain.usecase.GetPokemonListUseCase
import dev.najihhome.pokekuy.domain.usecase.LoginUserUseCase
import dev.najihhome.pokekuy.domain.usecase.RegisterUserUseCase
import dev.najihhome.pokekuy.domain.usecase.SearchPokemonUseCase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    // Local Database
    single { UserDatabase(get()) }
    single { PokemonDatabase(get()) }
    single { SessionManager(get()) }

    // Network Utilities
    single { NetworkUtil(get()) }

    // Network
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(PokemonApi::class.java) }

    // Repositories
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<PokemonRepository> { PokemonRepositoryImpl(get(), get(), get()) }

    // Use Cases
    single { RegisterUserUseCase(get()) }
    single { LoginUserUseCase(get()) }
    single { GetCurrentUserUseCase(get()) }
    single { GetPokemonListUseCase(get()) }
    single { GetPokemonDetailUseCase(get()) }
    single { SearchPokemonUseCase(get()) }
}
