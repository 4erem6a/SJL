package com.evg.sjl.ast.visitors

import com.evg.sjl.ast.*
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.*
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
        result.append("let $${statement.identifier}")
        if (statement.type != null)
            result.append(": ${type(statement.type)}")
        if (statement.initializer != null) {
            result.append(" = ")
            statement.initializer.accept(this)
        }
        result.appendln()
    }

    override fun visit(expression: AssignmentExpression) {
        expression.target.accept(this)
        result.append(" = ")
        expression.expression.accept(this)
    }

    override fun visit(expression: ArrayAccessExpression) {
        expression.array.accept(this)
        result.append("[")
        expression.key.accept(this)
        result.append("]")
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
            ARRAY_LENGTH -> result.append("length ")
        }
        expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression) {
        result.append("$${expression.identifier}")
    }

    override fun visit(expression: InputExpression) {
        result.append("input: ")
        result.append(type(expression.type))
    }

    override fun visit(expression: CastExpression) {
        result.append("(${type(expression.type)})")
        expression.expression.accept(this)
    }

    fun type(type: Type): String = when (type) {
        is Primitives -> "@${primitive(type)}"
        is StringType -> "@string"
        is JavaClass -> "@jvm(\"${type.name}\")"
        is ArrayType -> "${type(type.type)}[]"
        else -> "@?"
    }

    private fun primitive(type: Primitives): String = when (type) {
        Primitives.INTEGER -> "integer"
        Primitives.DOUBLE -> "double"
        Primitives.BOOLEAN -> "boolean"
    }

    override fun visit(statement: IfStatement) {
        result.append("if (")
        statement.condition.accept(this)
        result.append(")")
        if (statement.ifStatement !is UnionStatement
                && statement.ifStatement !is BlockStatement) {
            result.append("\n")
            indent(1)
            indentLevel++
        } else result.append(" ")
        statement.ifStatement.accept(this)
        if (statement.ifStatement !is UnionStatement
                && statement.ifStatement !is BlockStatement)
            indentLevel--
        if (statement.elseStatement == null)
            return
        indent()
        result.append("else ")
        statement.elseStatement?.accept(this)
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

    override fun visit(statement: WhileStatement) {
        result.append("while (")
        statement.condition.accept(this)
        result.append(")")
        if (statement.body !is UnionStatement
                && statement.body !is BlockStatement) {
            result.append("\n")
            indent(1)
            indentLevel++
        } else result.append(" ")
        statement.body.accept(this)
        if (statement.body !is UnionStatement
                && statement.body !is BlockStatement)
            indentLevel--
    }

    override fun visit(statement: DoWhileStatement) {
        result.append("do")
        if (statement.body !is UnionStatement
                && statement.body !is BlockStatement) {
            result.append("\n")
            indent(1)
            indentLevel++
        } else result.append(" ")
        statement.body.accept(this)
        if (statement.body !is UnionStatement
                && statement.body !is BlockStatement)
            indentLevel--
        result.append("while (")
        statement.condition.accept(this)
        result.append(")")
    }

    override fun visit(expression: ArrayExpression) {
        result.append("[")
        expression.values.forEachIndexed { i, e ->
            e.accept(this)
            if (i < expression.values.size - 1)
                result.append(", ")
        }
        result.append("]")
        if (expression.length != null) {
            result.append("(")
            expression.length.accept(this)
            result.append(")")
        }
        if (expression.type != null)
            result.append(" of ${type(expression.type)}")
    }

    override fun visit(expression: NewExpression) {
        result.append("new ${type(expression.type)}(")
        expression.args.forEachIndexed { i, e ->
            e.accept(this)
            if (i < expression.args.size - 1)
                result.append(", ")
        }
        result.append(")")
    }

    private fun indent(offset: Int = 0) {
        repeat((0 until indentLevel + offset).count()) {
            result.append("\t")
        }
    }
}