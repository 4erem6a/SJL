package com.evg.sjl.parser

import com.evg.sjl.exceptions.UnexpectedTokenException
import com.evg.sjl.lexer.Position
import com.evg.sjl.lexer.Token
import com.evg.sjl.lexer.TokenTypes
import com.evg.sjl.lexer.TokenTypes.*
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.*
import com.evg.sjl.parser.ast.*
import com.evg.sjl.values.*

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
        lookMatch(0, IDENTIFIER) && (lookMatch(1, EQ) || lookMatch(1, CM))  -> assignmentStatement()
        match(IF) -> ifStatement()
        match(LET) -> variableDefinitionStatement()
        lookMatch(0, AT) && lookMatch(1, LC) -> {
            consume(AT)
            consume(LC)
            val statements = ArrayList<Statement>()
            while (!match(RC))
                statements.add(statement())
            UnionStatement(statements)
        }
        match(LC) -> {
            val statements = ArrayList<Statement>()
            while (!match(RC))
                statements.add(statement())
            BlockStatement(statements)
        }
        else -> ExpressionStatement(expression())
    }

    private fun ifStatement(): Statement {
        consume(LP)
        val condition = expression()
        consume(RP)
        val ifStatement = statement()
        val elseStatement = if (match(ELSE))
            statement()
        else null
        return IfStatement(condition, ifStatement, elseStatement)
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
        val type = if (match(CL))
            type()
        else null
        val initializer = if (match(EQ))
            expression()
        else null
        return VariableDefinitionStatement(id, type, initializer)
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

    private fun expression(): Expression = Or()

    private fun Or(): Expression {
        var res = xor()
        loop@ while (true) res = when {
            match(VBVB) -> BinaryExpression(BOOLEAN_OR, res, xor())
            else -> break@loop
        }
        return res
    }

    private fun xor(): Expression {
        var res = and()
        loop@ while (true) res = when {
            match(CRCR) -> BinaryExpression(BOOLEAN_XOR, res, and())
            else -> break@loop
        }
        return res
    }

    private fun and(): Expression {
        var res = bitwiseOr()
        loop@ while (true) res = when {
            match(AMAM) -> BinaryExpression(BOOLEAN_AND, res, bitwiseOr())
            else -> break@loop
        }
        return res
    }

    private fun bitwiseOr(): Expression {
        var res = bitwiseXor()
        loop@ while (true) res = when {
            match(VB) -> BinaryExpression(BITWISE_OR, res, bitwiseXor())
            else -> break@loop
        }
        return res
    }

    private fun bitwiseXor(): Expression {
        var res = bitwiseAnd()
        loop@ while (true) res = when {
            match(CR) -> BinaryExpression(BITWISE_XOR, res, bitwiseAnd())
            else -> break@loop
        }
        return res
    }

    private fun bitwiseAnd(): Expression {
        var res = equality()
        loop@ while (true) res = when {
            match(AM) -> BinaryExpression(BITWISE_AND, res, equality())
            else -> break@loop
        }
        return res
    }

    private fun equality(): Expression {
        var res = relational()
        loop@ while (true) res = when {
            match(EQEQ) -> BinaryExpression(EQUALS, res, relational())
            match(EXEQ) -> BinaryExpression(NOT_EQUALS, res, relational())
            else -> break@loop
        }
        return res
    }

    private fun relational(): Expression {
        var res = shifts()
        loop@ while (true) res = when {
            match(LA) -> BinaryExpression(LOWER_THAN, res, shifts())
            match(RA) -> BinaryExpression(GREATER_THAN, res, shifts())
            match(LAEQ) -> BinaryExpression(EQUALS_OR_LOWER_THAN, res, shifts())
            match(RAEQ) -> BinaryExpression(EQUALS_OR_GREATER_THAN, res, shifts())
            else -> break@loop
        }
        return res
    }

    private fun shifts(): Expression {
        var res = additive()
        loop@ while (true) res = when {
            match(LALA) -> BinaryExpression(LEFT_SHIFT, res, additive())
            match(RARA) -> BinaryExpression(RIGHT_SHIFT, res, additive())
            match(RARARA) -> BinaryExpression(UNSIGNED_RIGHT_SHIFT, res, additive())
            else -> break@loop
        }
        return res
    }

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
        var res = cast()
        loop@ while (true) res = when {
            match(ST) -> BinaryExpression(MULTIPLICATION, res, cast())
            match(SL) -> BinaryExpression(DIVISION, res, cast())
            match(PR) -> BinaryExpression(REMAINDER, res, cast())
            else -> break@loop
        }
        return res
    }

    private fun cast(): Expression = when {
        match(LP) -> {
            val type = type()
            if (type == null) {
                position--
                unary()
            } else {
                consume(RP)
                CastExpression(type, unary())
            }
        }
        else -> unary()
    }

    private fun unary(): Expression = when {
        match(MN) -> UnaryExpression(NEGATION, primary())
        match(TL) -> UnaryExpression(BITWISE_NEGATION, primary())
        match(EX) -> UnaryExpression(BOOLEAN_NEGATION, primary())
        else -> {
            match(PL)
            primary()
        }
    }

    private fun primary(): Expression {
        val current = get(0)
        if (match(L_DOUBLE))
            return ValueExpression(DoubleValue(current.value.toDouble()))
        if (match(L_INTEGER))
            return ValueExpression(IntegerValue(current.value.toInt()))
        if (match(L_STRING))
            return ValueExpression(StringValue(current.value))
        if (match(TRUE))
            return ValueExpression(BooleanValue(true))
        if (match(FALSE))
            return ValueExpression(BooleanValue(false))
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
        match(T_BOOLEAN) -> Types.BOOLEAN
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