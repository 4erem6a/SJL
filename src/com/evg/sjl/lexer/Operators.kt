package com.evg.sjl.lexer

import com.evg.sjl.lexer.TokenTypes.*

object Operators {
    val map = mapOf(
            "+" to PL,
            "-" to MN,
            "*" to ST,
            "/" to SL,
            "%" to PR,
            "=" to EQ,
            "<<" to LALA,
            ">>" to RARA,
            ">>>" to RARARA,
            "|" to VB,
            "&" to AM,
            "^" to CR,
            "~" to TL,
            "(" to LP,
            ")" to RP,
            "{" to LC,
            "}" to RC,
            ":" to CL,
            "," to CM,
            ";" to SC
    )
    val characters = map.keys.reduce(String::plus).toSet()
}