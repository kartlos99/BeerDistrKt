package com.example.beerdistrkt.fragPages.orders

sealed class Event {
    object DuplicateBarrelItem: Event()
    object DuplicateBottleItem: Event()
//    object NoPriceException: Event()
//    object CustomerNotFount: Event()
}