package com.evg.sjl.parser

import com.evg.sjl.exceptions.UnexpectedTokenException
import com.evg.sjl.lexer.Position
import com.evg.sjl.lexer.Token
import com.evg.sjl.lexer.TokenTypes
import com.evg.sjl.lexer.TokenTypes.*
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*
import com.evg.sjl.values.DoubleValue
import com.evg.sjl.values.IntegerValue
import com.evg.sjl.values.StringValue
import com.evg.sjl.values.Types

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
        lookMatch(0, IDENTIFIER) && lookMatch(1, CL) -> variableDefinitionStatement()
        lookMatch(0, IDENTIFIER) && (lookMatch(1, EQ) || lookMatch(1, CM))  -> assignmentStatement()
        match(LC) -> {
            val statements = ArrayList<Statement>()
            while (!match(RC))
                statements.add(statement())
            UnionStatement(statements)
        }
        else -> ExpressionStatement(expression())
    }

    private fun printStatement(): Statement {
        val newLine = lookMatch(0, PRINTLN)
        consume()
        val statements = ArrayList<Statement>()
        do
            statements.add(PrintStatement(newLine, expression()))
        while (match(SC))
        return if (statements.size == 1)
            statements.first()
        else UnionStatement(statements)
    }

    private fun variableDefinitionStatement(): Statement {
        val id = consume(IDENTIFIER).value
        consume(CL)
        val type = when {
            match(T_DOUBLE) -> Types.DOUBLE
            match(T_INTEGER) -> Types.INTEGER
            match(T_STRING) -> Types.STRING
            else -> throw UnexpectedTokenException(get(), T_DOUBLE, T_STRING)
        }
        if (match(EQ)) return UnionStatement(listOf(
                VariableDefinitionStatement(id, type),
                AssignmentStatement(id, expression())
        ))
        return VariableDefinitionStatement(id, type)
    }

    private fun assignmentStatement(): Statement {
        val ids = ArrayList<String>()
        do
            ids.add(consume(IDENTIFIER).value)
        while (match(CM))
        consume(EQ)
        val expression = expression()
        val statements = ArrayList<Statement>()
        for (id in ids)
            statements.add(AssignmentStatement(id, expression))
        return UnionStatement(statements)
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
            match(PR) -> BinaryExpression(REMAINDER, res, unary())
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
        if (match(L_DOUBLE))
            return ValueExpression(DoubleValue(current.value.toDouble()))
        if (match(L_INTEGER))
            return ValueExpression(IntegerValue(current.value.toInt()))
        if (match(L_STRING))
            return ValueExpression(StringValue(current.value))
        if (match(IDENTIFIER))
            return VariableExpression(current.value)
        if (match(INPUT))
            return inputExpression()
        if (match(LP)) {
            val result = expression()
            consume(RP)
            return result
        }
        throw UnexpectedTokenException(current)
    }

    private fun inputExpression(): Expression {
        consume(CL)
        val type = type() ?: throw UnexpectedTokenException(get())
        return InputExpression(type)
    }

    private fun type(): Types? = when {
        match(T_DOUBLE) -> Types.DOUBLE
        match(T_INTEGER) -> Types.INTEGER
        match(T_STRING) -> Types.STRING
        else -> null
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