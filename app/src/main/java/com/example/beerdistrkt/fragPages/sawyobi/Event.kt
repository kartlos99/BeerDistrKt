package com.example.beerdistrkt.fragPages.sawyobi

sealed class Event {
    object DuplicateBarrelItem: Event()
    object DuplicateBottleItem: Event()
}