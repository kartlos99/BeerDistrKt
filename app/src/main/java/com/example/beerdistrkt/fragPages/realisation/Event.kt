package com.example.beerdistrkt.fragPages.realisation

sealed class Event {
    object DuplicateBarrelItem: Event()
    object DuplicateBottleItem: Event()
    object NoPriceException: Event()
    object CustomerNotFount: Event()
}