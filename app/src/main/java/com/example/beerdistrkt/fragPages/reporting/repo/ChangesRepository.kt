package com.example.beerdistrkt.fragPages.reporting.repo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChangesRepository {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    suspend fun changesList(): Flow<List<String>> {


        return flow { demoList }
    }

    private val demoList = listOf("kasri", "meore", "didi")
}