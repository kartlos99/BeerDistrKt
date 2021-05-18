package com.example.beerdistrkt.models

data class ClientDeactivateModel(
    val clientID: Int,
    val allRegions: Boolean = false
)

data class AttachRegionsRequest(
    val clientID: Int,
    val regionIDs: List<String>
)