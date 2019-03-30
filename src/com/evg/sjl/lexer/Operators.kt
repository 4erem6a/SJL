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
            "++" to PLPL,
            "--" to MNMN,
            "<<" to LALA,
            ">>" to RARA,
            ">>>" to RARARA,
            "|" to VB,
            "&" to AM,
            "^" to CR,
            "~" to TL,
            "==" to EQEQ,
            "!=" to EXEQ,
            "<" to LA,
            ">" to RA,
            "<=" to LAEQ,
            ">=" to RAEQ,
            "||" to VBVB,
            "&&" to AMAM,
            "^^" to CRCR,
            "!" to EX,
            "(" to LP,
            ")" to RP,
            "[" to LB,
            "]" to RB,
            "{" to LC,
            "@{" to ATLC,
            "}" to RC,
            ":" to CL,
            "," to CM,
            ";" to SC
    )
    val characters = map.keys.reduce(String::plus).toSet()
}