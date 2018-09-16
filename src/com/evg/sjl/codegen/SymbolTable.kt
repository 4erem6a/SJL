package com.evg.sjl.codegen

import com.evg.sjl.values.Types

class SymbolTable {
    val symbols = LinkedHashMap<String, Symbol>()
    fun register(identifier: String, type: Types): Symbol {
        if (identifier !in symbols) {
            val index = when {
                symbols.isEmpty() -> 1
                else -> symbols.values.last().index +
                        if (symbols.values.last().type == Types.DOUBLE)
                            2
                        else 1
            }
            symbols[identifier] = Symbol(index, type)
        }
        return get(identifier)!!
    }

    operator fun get(identifier: String): Symbol? = symbols[identifier]
}

data class Symbol(val index: Int, val type: Types)