package com.evg.sjl.exceptions

import com.evg.sjl.lexer.Token
import com.evg.sjl.lexer.TokenTypes

abstract class SyntaxException(message: String) : SJLException(message)

class UnexpectedTokenException(got: Token, vararg expected: TokenTypes? = arrayOf())
    : SyntaxException(
        if (expected.isEmpty())
            "Unexpected token ${got.type} at ${got.position}"
        else "Unexpected token ${got.type} at ${got.position}: expected ${expected.joinToString(" or ")}"
)