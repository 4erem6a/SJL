package com.evg.sjl.parser.ast

import com.evg.sjl.lib.BinaryOperations
import com.evg.sjl.lib.UnaryOperations
import com.evg.sjl.parser.visitors.Visitor

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

data class AssignmentStatement(val identifier: String, val expression: Expression) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

data class UnionStatement(val statements: List<Statement>) : Statement {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}

interface Expression : Node

data class NumberExpression(val number: Double) : Expression {
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

data class InputExpression(val charMode: Boolean) : Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)
}