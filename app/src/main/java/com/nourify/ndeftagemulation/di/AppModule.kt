package com.nourify.ndeftagemulation.di

import android.content.Context
import androidx.room.Database
import com.nourify.ndeftagemulation.data.storage.AppDatabase
import com.nourify.ndeftagemulation.data.storage.NdefTagDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.nourify.ndeftagemulation")
class AppModule {

    @Single
    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

    @Single
    fun provideNdefTagDao(database: AppDatabase): NdefTagDao {
        return database.ndefTagDao()
    }

}
