package com.evg.sjl.parser.visitors

import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*
import com.evg.sjl.values.NumberValue
import com.evg.sjl.values.StringValue
import com.evg.sjl.values.Types

class PrintVisitor : Visitor {
    private val result = StringBuilder()
    private var indentLevel = 0

    fun clear() = result.setLength(0)

    override fun toString() = result.toString()

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
        result.appendln()
    }

    override fun visit(statement: PrintStatement) {
        result.append("print")
        if (statement.newLine)
            result.append("ln")
        if (statement.charMode)
            result.append(" char")
        result.append(" ")
        statement.expression.accept(this)
        result.appendln()
    }

    override fun visit(statement: VariableDefinitionStatement) {
        result.append("$${statement.identifier} : ")
        result.append(when (statement.type) {
            Types.NUMBER -> "number"
            Types.STRING -> "string"
        })
        result.appendln()
    }

    override fun visit(statement: AssignmentStatement) {
        result.append("$${statement.identifier} = ")
        statement.expression.accept(this)
        result.appendln()
    }

    override fun visit(statement: UnionStatement) {
        if (statement.statements.isEmpty()) {
            result.appendln("{ }")
            return
        }
        indentLevel++
        result.appendln("{")
        for (stmt in statement.statements) {
            (0 until indentLevel).forEach { result.append('\t') }
            stmt.accept(this)
        }
        indentLevel--
        (0 until indentLevel).forEach { result.append('\t') }
        result.appendln("}")
    }

    override fun visit(expression: ValueExpression) {
        when (expression.value) {
            is NumberValue -> result.append(expression.value.value)
            is StringValue -> result.append("\"${expression.value.value}\"")
        }
    }

    override fun visit(expression: BinaryExpression) {
        result.append("(")
        expression.left.accept(this)
        result.append(when (expression.operation) {
            ADDITION -> " + "
            SUBTRACTION -> " - "
            MULTIPLICATION -> " * "
            DIVISION -> " / "
            REMAINDER -> " % "
        })
        expression.right.accept(this)
        result.append(")")
    }

    override fun visit(expression: UnaryExpression) {
        if (expression.operation == NEGATION)
            result.append("-")
        expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression) {
        result.append("$${expression.identifier}")
    }

    override fun visit(expression: InputExpression) {
        result.append("input")
        if (expression.charMode)
            result.append(" char")
    }
}