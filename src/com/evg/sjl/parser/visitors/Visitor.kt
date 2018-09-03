package com.evg.sjl.parser.visitors

import com.evg.sjl.parser.ast.*

interface Visitor {
    fun visit(statement: ExpressionStatement)
    fun visit(statement: PrintStatement)
    fun visit(statement: UnionStatement)
    fun visit(statement: VariableDefinitionStatement)
    fun visit(statement: AssignmentStatement)

    fun visit(expression: NumberExpression)
    fun visit(expression: BinaryExpression)
    fun visit(expression: UnaryExpression)
    fun visit(expression: VariableExpression)
    fun visit(expression: InputExpression)
}