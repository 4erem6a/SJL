package com.evg.sjl.lexer

import com.evg.sjl.lexer.TokenTypes.*

class Operators {
    companion object {
        val map = mapOf(
                "+" to PL,
                "-" to MN,
                "*" to ST,
                "/" to SL,
                "%" to PR,
                "=" to EQ,
                "(" to LP,
                ")" to RP,
                ";" to SC
        )
        val characters = map.keys.reduce(String::plus).toSet()
    }
}