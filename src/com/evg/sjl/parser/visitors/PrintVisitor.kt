package com.evg.sjl.parser.visitors

import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*

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

    override fun visit(expression: AssignmentStatement) {
        result.append("$${expression.identifier} = ")
        expression.expression.accept(this)
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

    override fun visit(expression: NumberExpression) {
        result.append(expression.number)
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