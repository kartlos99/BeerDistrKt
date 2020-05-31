package com.example.beerdistrkt.storage

import kotlin.reflect.KClass

class ObjectCache {

    private val map = mutableMapOf<String, Any>()

    fun <T : Any> put(clazz: KClass<T>, id: String = "", obj: T) {
        map[getKey(clazz, id)] = obj
    }

    fun <T : Any> get(clazz: KClass<T>, id: String = ""): T? =
        map[getKey(clazz, id)] as? T

    fun <T : Any> clear(clazz: KClass<T>, id: String = "") {
        map.remove(getKey(clazz, id))
    }

    fun <T : Any> clearList(clazz: KClass<T>) {
        map.remove(getKey(clazz, "List"))
    }

    fun <T : Any> putList(clazz: KClass<T>, id: String = "List", obj: List<T>) {
        map[getKey(clazz, id)] = obj
    }

    fun <T : Any> getList(clazz: KClass<T>, id: String = "List"): List<T>? =
        map[getKey(clazz, id)] as? List<T>

    private fun <T : Any> getKey(clazz: KClass<T>, id: String = "") = clazz.qualifiedName + "_" + id

    fun clean() {
        map.clear()
    }
}