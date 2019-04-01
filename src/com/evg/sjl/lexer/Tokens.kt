package com.evg.sjl.lexer

enum class TokenTypes {
    IDENTIFIER,         //\$[a-zA-Z_][\w]*

    L_STRING,           //".*"
    L_INTEGER,          //[0-9]+
    L_DOUBLE,           //[0-9]+\.[0-9]+

    EQ,                 //=
    SL,                 ///
    ST,                 //*
    MN,                 //-
    PL,                 //+
    PR,                 //%

    PLPL,               //++
    MNMN,               //--

    LALA,               //<<
    RARA,               //>>
    RARARA,             //>>>

    VB,                 //|
    AM,                 //&
    CR,                 //^
    TL,                 //~

    EQEQ,               //==
    EXEQ,               //!=

    LA,                 //<
    RA,                 //>
    LAEQ,               //<=
    RAEQ,               //>=

    VBVB,               //||
    AMAM,               //&&
    CRCR,               //^^

    EX,                 //!

    CL,                 //:
    CM,                 //,
    SC,                 //;

    LP,                 //(
    RP,                 //)

    LB,                 //[
    RB,                 //]

    LC,                 //{
    ATLC,               //@{
    RC,                 //}

    PRINT,              //print
    PRINTLN,            //println
    CHAR,               //char
    INPUT,              //input
    IF,                 //if
    ELSE,               //else
    LET,                //let
    WHILE,              //while
    DO,                 //do
    FOR,                //for
    OF,                 //of
    NEW,                //new
    LENGTH,             //length

    TRUE,               //true
    FALSE,              //false

    T_STRING,           //@string
    T_INTEGER,          //@integer
    T_DOUBLE,           //@double
    T_BOOLEAN,          //@boolean

    EOF
}

data class Position(val line: Int, val col: Int, val abs: Int) {
    override fun toString() = "[$line,$col:$abs]"
}

data class Token(val type: TokenTypes, val value: String, val position: Position) {
    override fun toString() = "$type($value)$position"
}