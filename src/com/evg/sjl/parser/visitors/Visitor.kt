package com.evg.sjl.parser.visitors

import com.evg.sjl.parser.ast.*

interface Visitor {
    fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    fun visit(statement: PrintStatement) {
        statement.expression.accept(this)
    }

    fun visit(statement: UnionStatement) {
        statement.statements.forEach { it.accept(this) }
    }

    fun visit(statement: VariableDefinitionStatement) {}

    fun visit(statement: AssignmentStatement) {
        statement.expression.accept(this)
    }


    fun visit(expression: ValueExpression) {}

    fun visit(expression: BinaryExpression) {
        expression.left.accept(this)
        expression.right.accept(this)
    }

    fun visit(expression: UnaryExpression) {
        expression.expression.accept(this)
    }

    fun visit(expression: VariableExpression) {}

    fun visit(expression: InputExpression) {}

    fun visit(expression: CastExpression) {
        expression.expression.accept(this)
    }

    fun visit(statement: IfStatement) {
        statement.condition.accept(this)
        statement.ifStatement.accept(this)
        statement.elseStatement?.accept(this)
    }
}