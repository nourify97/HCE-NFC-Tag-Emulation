package com.nourify.ndeftagemulation.data

import com.nourify.ndeftagemulation.data.storage.NdefTag
import com.nourify.ndeftagemulation.data.storage.NdefTagDao
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class NdefTagRepo(
    private val ndefTagDao: NdefTagDao,
) {
    suspend fun insert(tag: NdefTag) = ndefTagDao.insert(tag)

    fun getAll(): Flow<List<NdefTag>> = ndefTagDao.getAll()

    suspend fun delete(tag: NdefTag) = ndefTagDao.delete(tag)
}
