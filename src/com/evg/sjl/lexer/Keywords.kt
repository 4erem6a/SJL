package com.evg.sjl.lexer

import com.evg.sjl.lexer.TokenTypes.*

object Keywords {
    val map = mapOf(
            "print" to PRINT,
            "println" to PRINTLN,
            "char" to CHAR,
            "input" to INPUT,
            "true" to TRUE,
            "false" to FALSE,
            "if" to IF,
            "else" to ELSE,
            "let" to LET,
            "while" to WHILE,
            "do" to DO,
            "for" to FOR
    )
}