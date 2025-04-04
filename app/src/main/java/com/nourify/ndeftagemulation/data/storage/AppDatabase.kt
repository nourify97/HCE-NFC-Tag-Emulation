package com.nourify.ndeftagemulation.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [NdefTag::class], version = 1, exportSchema = false)
@TypeConverters(NdefMessageConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ndefTagDao(): NdefTagDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room
                .databaseBuilder(
                    context = context,
                    klass = AppDatabase::class.java,
                    "app_database",
                ).fallbackToDestructiveMigration()
                .build()
    }
}
