package dev.najihhome.pokekuy

import android.app.Application
import dev.najihhome.pokekuy.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PokeKuyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@PokeKuyApplication)
            modules(appModule)
        }
    }
}
