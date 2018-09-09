package com.evg.sjl.lexer

enum class TokenTypes {
    IDENTIFIER,         //\$[a-zA-Z_][\w]*
    NUMBER,             //[0-9]+(?:\.[0-9]+)?
    STRING,             //".*"

    EQ,                 //=
    SL,                 ///
    ST,                 //*
    MN,                 //-
    PL,                 //+
    PR,                 //%
    CL,                 //:
    CM,                 //,
    SC,                 //;

    LP,                 //(
    RP,                 //)

    LC,                 //{
    RC,                 //}

    PRINT,              //print
    PRINTLN,            //println
    CHAR,               //char
    INPUT,              //input

    T_NUMBER,           //number
    T_STRING,           //string

    EOF
}

data class Position(val line: Int, val col: Int, val abs: Int) {
    override fun toString() = "[$line,$col:$abs]"
}

data class Token(val type: TokenTypes, val value: String, val position: Position)