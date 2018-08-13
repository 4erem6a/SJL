package com.evg.sjl.parser

import com.evg.sjl.exceptions.UnexpectedTokenException
import com.evg.sjl.lexer.Position
import com.evg.sjl.lexer.Token
import com.evg.sjl.lexer.TokenTypes
import com.evg.sjl.lexer.TokenTypes.*
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*

class Parser(private val tokens: List<Token>) {
    companion object {
        private val EOF = Token(TokenTypes.EOF, "", Position(-1, -1, -1))
    }

    private val size = tokens.size
    private var position = 0

    fun parse(): UnionStatement {
        val result = ArrayList<Statement>()
        while (!match(TokenTypes.EOF))
            result.add(statement())
        return UnionStatement(result)
    }

    private fun statement(): Statement = when {
        lookMatch(0, PRINT) || lookMatch(0, PRINTLN) -> printStatement()
        lookMatch(0, IDENTIFIER) && lookMatch(1, EQ) -> assignmentStatement()
        else -> ExpressionStatement(expression())
    }

    private fun printStatement(): Statement {
        val newLine = lookMatch(0, PRINTLN)
        consume()
        val charMode = match(CHAR)
        val statements = ArrayList<Statement>()
        do
            statements.add(PrintStatement(newLine, charMode, expression()))
        while (match(SC))
        return if (statements.size == 1)
            statements.first()
        else UnionStatement(statements)
    }

    private fun assignmentStatement(): Statement {
        val id = consume(IDENTIFIER).value
        consume(EQ)
        return AssignmentStatement(id, expression())
    }

    private fun expression(): Expression = additive()

    private fun additive(): Expression {
        var res = multiplicative()
        loop@ while (true) res = when {
            match(PL) -> BinaryExpression(ADDITION, res, multiplicative())
            match(MN) -> BinaryExpression(SUBTRACTION, res, multiplicative())
            else -> break@loop
        }
        return res
    }

    private fun multiplicative(): Expression {
        var res = unary()
        loop@ while (true) res = when {
            match(ST) -> BinaryExpression(MULTIPLICATION, res, unary())
            match(SL) -> BinaryExpression(DIVISION, res, unary())
            else -> break@loop
        }
        return res
    }

    private fun unary(): Expression {
        if (match(MN))
            return UnaryExpression(NEGATION, primary())
        match(PL)
        return primary()
    }

    private fun primary(): Expression {
        val current = get(0)
        if (match(NUMBER))
            return NumberExpression(current.value.toDouble())
        if (match(IDENTIFIER))
            return VariableExpression(current.value)
        if (match(INPUT))
            return InputExpression(match(CHAR))
        if (match(LP)) {
            val result = expression()
            consume(RP)
            return result
        }
        throw UnexpectedTokenException(current)
    }

    private fun consume(): Token {
        position++
        return get(0)
    }

    private fun consume(type: TokenTypes): Token {
        val current = get(0)
        if (current.type != type)
            throw UnexpectedTokenException(current, type)
        position++
        return current
    }

    private fun match(type: TokenTypes): Boolean {
        if (get(0).type != type)
            return false
        position++
        return true
    }

    private fun lookMatch(offset: Int, type: TokenTypes) =
            get(offset).type == type

    private fun get(offset: Int = 0) = if (position + offset >= size)
        EOF
    else tokens[position + offset]
}