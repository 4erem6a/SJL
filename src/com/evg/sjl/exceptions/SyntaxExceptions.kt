package com.evg.sjl.exceptions

import com.evg.sjl.lexer.Token
import com.evg.sjl.lexer.TokenTypes

abstract class SyntaxException(message: String) : SJLException(message)

class UnexpectedTokenException(got: Token, expected: TokenTypes? = null)
    : SyntaxException(
        if (expected == null)
            "Unexpected token ${got.type} at ${got.position}"
        else "Unexpected token ${got.type} at ${got.position}: expected $expected"
)