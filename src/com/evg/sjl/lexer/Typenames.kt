package com.evg.sjl.lexer

import com.evg.sjl.lexer.TokenTypes.*

object Typenames {
    val map = mapOf(
            "integer" to T_INTEGER,
            "int" to T_INTEGER,
            "boolean" to T_BOOLEAN,
            "bool" to T_BOOLEAN,
            "double" to T_DOUBLE,
            "real" to T_DOUBLE,
            "string" to T_STRING
    )
}