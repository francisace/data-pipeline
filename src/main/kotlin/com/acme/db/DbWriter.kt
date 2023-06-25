package com.acme.db

interface DbWriter<V> {
    fun write(itemList: Iterable<V>) : Int
}