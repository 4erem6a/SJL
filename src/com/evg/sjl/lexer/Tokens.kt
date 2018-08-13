package com.evg.sjl.lexer

enum class TokenTypes {
    IDENTIFIER,         //\$[a-zA-Z_][\w]*
    NUMBER,             //[0-9]+(?:\.[0-9]+)?

    EQ,                 //=
    SL,                 ///
    ST,                 //*
    MN,                 //-
    PL,                 //+
    PR,                 //%
    SC,                 //;

    LP,                 //(
    RP,                 //)

    LC,                 //{
    RC,                 //}

    PRINT,              //print
    PRINTLN,            //println
    CHAR,               //char
    INPUT,              //input

    EOF
}

data class Position(val line: Int, val col: Int, val abs: Int) {
    override fun toString() = "[$line,$col:$abs]"
}

data class Token(val type: TokenTypes, val value: String, val position: Position)