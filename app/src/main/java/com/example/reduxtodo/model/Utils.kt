package com.example.reduxtodo.model

fun <T> List<T>.remove(index: Int): List<T> = filterIndexed { i, _ -> i != index }

fun <T> List<T>.set(index: Int, newValue: T): List<T> =
    mapIndexed { i, value -> if (i == index) newValue else value }

fun <T> List<T>.edit(index: Int, transform: (T) -> T): List<T> =
    mapIndexed { i, value -> if (i == index) transform(value) else value }
