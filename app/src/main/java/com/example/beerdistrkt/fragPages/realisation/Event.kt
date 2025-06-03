package com.example.beerdistrkt.fragPages.realisation

sealed class Event {
    data object DuplicateBarrelItem: Event()
    data object DuplicateBottleItem: Event()
    data object NoPriceException: Event()
    data object CustomerNotFount: Event()
    data object EmptyFormError: Event()
}