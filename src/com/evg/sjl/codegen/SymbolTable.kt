package com.evg.sjl.codegen

class SymbolTable {
    val symbols = LinkedHashMap<String, Int>()
    fun register(identifier: String): Int {
        val value = if (symbols.isEmpty()) 1 else symbols.values.last() + 2 // +2 because of double type
        symbols[identifier] = value
        return value
    }

    operator fun get(identifier: String) = symbols[identifier]
}