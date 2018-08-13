package com.evg.sjl.lexer

import com.evg.sjl.exceptions.InvalidTokenDefinitionException
import com.evg.sjl.exceptions.UnknownCharacterException
import com.evg.sjl.exceptions.UnknownKeywordException
import com.evg.sjl.exceptions.UnknownOperatorException
import com.evg.sjl.lexer.TokenTypes.IDENTIFIER
import com.evg.sjl.lexer.TokenTypes.NUMBER

class Lexer(private val source: String) {
    private val length = source.length
    private var position = 0
    private val tokens = ArrayList<Token>()

    fun tokenize(): List<Token> {
        var current = peek()
        while (!isEnd()) {
            when {
                current == '#' -> comment()
                current.isDigit() -> number()
                current == '$' -> identifier()
                current.isLetter() -> keyword()
                current in Operators.characters -> operator()
                current.isWhitespace() -> skip()
                current == '\'' -> character()
                else -> throw UnknownCharacterException(current, position())
            }
            current = peek()
        }
        return tokens
    }

    private fun character() {
        val pos = position()
        skip()
        val char = peek()
        if (skip() != '\'')
            throw InvalidTokenDefinitionException(NUMBER, pos)
        skip()
        add(Token(NUMBER, char.toInt().toString(), pos))
    }

    private fun comment() {
        while (!isEnd() && skip() != '\n');
        skip()
    }

    private fun number() {
        val pos = position()
        var buffer = ""
        var current = peek()
        loop@ while (!isEnd()) {
            when {
                current.isDigit() -> buffer += current
                current == '.' -> if (current in buffer)
                    throw InvalidTokenDefinitionException(NUMBER, pos)
                else buffer += current
                else -> if (current != '_')
                    break@loop
            }
            current = skip()
        }
        add(Token(NUMBER, buffer, pos))
    }

    private fun identifier() {
        val pos = position()
        skip()
        add(Token(IDENTIFIER, word(), pos))
    }

    private fun keyword() {
        val pos = position()
        val word = word()
        val type = Keywords.map[word]
                ?: throw UnknownKeywordException(word, pos)
        add(Token(type, word, pos))
    }

    private fun word(): String {
        var buffer = ""
        var current = peek()
        while (!isEnd() && (current.isLetterOrDigit() || current == '_')) {
            buffer += current
            current = skip()
        }
        return buffer
    }

    private fun operator() {
        val pos = position()
        var buffer = ""
        var current = peek()
        while (!isEnd() && current in Operators.characters) {
            if (buffer in Operators.map && (buffer + current) !in Operators.map)
                break
            buffer += current
            current = skip()
        }
        val type = Operators.map[buffer]
                ?: throw UnknownOperatorException(buffer, pos)
        add(Token(type, buffer, pos))
    }

    private fun position(): Position {
        val substring = if (position == length - 1)
            source.substring(0)
        else source.substring(0, position)
        val line = substring.count { it == '\n' } + 1
        val col = if (substring.lastIndexOf('\n') != -1)
            substring.substring(substring.lastIndexOf('\n')).length - 1
        else substring.length
        return Position(line, col, position + 1)
    }

    private fun peek(offset: Int = 0) = if (position + offset < length)
        source[position + offset]
    else '\u0000'

    private fun skip(count: Int = 1): Char {
        position += count
        return peek()
    }

    private fun add(token: Token) = tokens.add(token)

    private fun isEnd() = position >= length
}