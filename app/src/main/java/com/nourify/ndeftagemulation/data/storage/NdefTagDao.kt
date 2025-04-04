package com.nourify.ndeftagemulation.data.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NdefTagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: NdefTag)

    @Query("SELECT * FROM ndefTags")
    fun getAll(): Flow<List<NdefTag>>

    @Delete
    suspend fun delete(ndefTag: NdefTag)
}
