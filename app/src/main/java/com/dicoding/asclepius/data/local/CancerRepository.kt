package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.room.CancerDao
import com.dicoding.asclepius.data.local.entity.CancerEntity

class CancerRepository private constructor(
    private val cancerDao: CancerDao
) {

    fun getCancer(): LiveData<List<CancerEntity>> = cancerDao.getCancer()

    suspend fun insertCancers(cancers: List<CancerEntity>) {
        cancerDao.insertCancer(cancers)
    }

    companion object {
        @Volatile
        private var instance: CancerRepository? = null
        fun getInstance(
            cancerDao: CancerDao
        ): CancerRepository =
            instance ?: synchronized(this) {
                instance ?: CancerRepository(cancerDao)
            }.also { instance = it }
    }
}