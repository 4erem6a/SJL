package com.evg.sjl.parser.ast

import com.evg.sjl.lib.BinaryOperations
import com.evg.sjl.lib.UnaryOperations
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import com.evg.sjl.values.Value

interface Node {
    fun accept(visitor: Visitor)
}

interface Statement : Node

data class ExpressionStatement(val expression: Expression) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class PrintStatement(val newLine: Boolean, val charMode: Boolean, val expression: Expression) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class VariableDefinitionStatement(val identifier: String, val type: Types) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class AssignmentStatement(val identifier: String, val expression: Expression) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class UnionStatement(val statements: List<Statement>) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

interface Expression : Node

data class ValueExpression(val value: Value) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class BinaryExpression(val operation: BinaryOperations,
                            val left: Expression, val right: Expression) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class UnaryExpression(val operation: UnaryOperations,
                           val expression: Expression) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class VariableExpression(val identifier: String) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class InputExpression(val type: Types, val charMode: Boolean = false) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}