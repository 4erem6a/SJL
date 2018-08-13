package com.evg.sjl.parser.visitors

import com.evg.sjl.parser.ast.*

interface Visitor {
    fun visit(statement: ExpressionStatement)
    fun visit(statement: PrintStatement)
    fun visit(statement: UnionStatement)

    fun visit(expression: NumberExpression)
    fun visit(expression: BinaryExpression)
    fun visit(expression: UnaryExpression)
    fun visit(expression: AssignmentStatement)
    fun visit(expression: VariableExpression)
    fun visit(expression: InputExpression)
}