package com.evg.sjl.codegen

import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Type

class SymbolTable {
    class SymbolScope(var subscope: SymbolScope? = null) {
        val symbols = HashMap<String, Symbol>()

        fun get(identifier: String): Symbol?
            = symbols[identifier] ?: subscope?.get(identifier)
    }

    var topScope = SymbolScope()
    private var lastSymbol: Symbol = Symbol(0, Primitives.INTEGER)

    fun register(identifier: String, type: Type): Symbol {
        val existing = topScope.symbols[identifier]
        if (existing != null)
            return existing
        val index = lastSymbol.index + when (lastSymbol.type) {
            Primitives.DOUBLE -> 2
            else -> 1
        }
        val symbol = Symbol(index, type)
        topScope.symbols[identifier] = symbol
        lastSymbol = symbol
        return symbol
    }

    fun upScope() {
        topScope = SymbolScope(topScope)
    }

    fun downScope() {
        topScope = topScope.subscope ?: topScope
    }

    operator fun get(identifier: String): Symbol? = topScope.get(identifier)

    fun getTop(identifier: String): Symbol? = topScope.symbols[identifier]
}

data class Symbol(val index: Int, val type: Type)