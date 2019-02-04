package com.evg.sjl.parser.visitors

import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.*
import com.evg.sjl.parser.ast.*
import com.evg.sjl.values.*

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
        result.append(" ")
        statement.expression.accept(this)
        result.appendln()
    }

    override fun visit(statement: VariableDefinitionStatement) {
        result.append("$${statement.identifier} : ")
        result.append(type(statement.type))
        result.appendln()
    }

    override fun visit(statement: AssignmentStatement) {
        result.append("$${statement.identifier} = ")
        statement.expression.accept(this)
        result.appendln()
    }

    override fun visit(statement: UnionStatement) {
        if (statement.statements.isEmpty()) {
            result.appendln("@{ }")
            return
        }
        indentLevel++
        result.appendln("@{")
        for (stmt in statement.statements) {
            indent()
            stmt.accept(this)
        }
        indentLevel--
        indent()
        result.appendln("}")
    }

    override fun visit(expression: ValueExpression) {
        when (expression.value) {
            is DoubleValue -> result.append((expression.value as DoubleValue).value)
            is IntegerValue -> result.append((expression.value as IntegerValue).value)
            is StringValue -> result.append("\"${(expression.value as StringValue).value}\"")
            is BooleanValue -> result.append(if ((expression.value as BooleanValue).value) "true" else "false")
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
            RIGHT_SHIFT -> " >> "
            LEFT_SHIFT -> " << "
            UNSIGNED_RIGHT_SHIFT -> " >>> "
            BITWISE_AND -> " & "
            BITWISE_XOR -> " ^ "
            BITWISE_OR -> " | "
            EQUALS -> " == "
            LOWER_THAN -> " < "
            GREATER_THAN -> " > "
            EQUALS_OR_LOWER_THAN -> " <= "
            EQUALS_OR_GREATER_THAN -> " >= "
            BOOLEAN_AND -> " && "
            BOOLEAN_OR -> " || "
            BOOLEAN_XOR -> " ^^ "
            NOT_EQUALS -> " != "
        })
        expression.right.accept(this)
        result.append(")")
    }

    override fun visit(expression: UnaryExpression) {
        when (expression.operation) {
            NEGATION -> result.append("-")
            BITWISE_NEGATION -> result.append("~")
            BOOLEAN_NEGATION -> result.append("!")
        }
        expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression) {
        result.append("$${expression.identifier}")
    }

    override fun visit(expression: InputExpression) {
        result.append("input")
        result.append(':')
        result.append(type(expression.type))
    }

    override fun visit(expression: CastExpression) {
        result.append("(${expression.type.toString().toLowerCase()})")
        expression.expression.accept(this)
    }

    fun type(type: Types): String = when (type) {
        Types.INTEGER -> "integer"
        Types.DOUBLE -> "double"
        Types.STRING -> "string"
        Types.BOOLEAN -> "boolean"
    }

    override fun visit(statement: IfStatement) {
        result.append("if (")
        statement.condition.accept(this)
        result.append(")")
        if (statement.ifStatement !is UnionStatement) {
            result.append("\n")
            indent(1)
            indentLevel++
        } else result.append(" ")
        statement.ifStatement.accept(this)
        if (statement.ifStatement !is UnionStatement)
            indentLevel--
        if (statement.elseStatement == null)
            return
        indent()
        result.append("else ")
        statement.elseStatement?.accept(this)
    }

    private fun indent(offset: Int = 0) {
        repeat((0 until indentLevel + offset).count()) {
            result.append("\t")
        }
    }

    override fun visit(statement: BlockStatement) {
        if (statement.statements.isEmpty()) {
            result.appendln("{ }")
            return
        }
        indentLevel++
        result.appendln("{")
        for (stmt in statement.statements) {
            indent()
            stmt.accept(this)
        }
        indentLevel--
        indent()
        result.appendln("}")
    }
}