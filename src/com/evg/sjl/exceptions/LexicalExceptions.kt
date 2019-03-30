package com.evg.sjl.exceptions

import com.evg.sjl.lexer.Position
import com.evg.sjl.lexer.TokenTypes

abstract class LexicalException(message: String) : SJLException(message)

class UnknownCharacterException(character: Char, position: Position)
    : LexicalException("Unknown character '$character' at $position")

class InvalidTokenDefinitionException(type: TokenTypes, position: Position)
    : LexicalException("Invalid $type token definition at $position")

class UnknownKeywordException(word: String, position: Position)
    : LexicalException("Unknown keyword '$word' at $position")

class UnknownOperatorException(operator: String, position: Position)
    : LexicalException("Unknown operator '$operator' at $position")

class UnknownTypenameException(typename: String, position: Position)
    : LexicalException("Unknown typename @$typename at $position")