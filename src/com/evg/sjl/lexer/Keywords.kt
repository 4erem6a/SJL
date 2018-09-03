package com.evg.sjl.lexer

import com.evg.sjl.lexer.TokenTypes.*

class Keywords {
    companion object {
        val map = mapOf(
                "print" to PRINT,
                "println" to PRINTLN,
                "char" to CHAR,
                "input" to INPUT,
                "number" to T_NUMBER,
                "string" to T_STRING
        )
    }
}