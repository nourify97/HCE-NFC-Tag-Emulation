package com.nourify.ndeftagemulation.di

import android.content.Context
import com.nourify.ndeftagemulation.data.storage.AppDatabase
import com.nourify.ndeftagemulation.data.storage.NdefTagDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.nourify.ndeftagemulation")
class AppModule {
    @Single
    fun provideDatabase(context: Context): AppDatabase = AppDatabase.create(context)

    @Single
    fun provideNdefTagDao(database: AppDatabase): NdefTagDao = database.ndefTagDao()
}
