package com.example.beerdistrkt.fragPages.reporting.model

data class HistoryDto(
    val history: List<Map<String, String>>,
    val users: Map<String, String>? = null,
    val customers: Map<String, String>? = null,
    val barrels: Map<String, String>? = null,
)