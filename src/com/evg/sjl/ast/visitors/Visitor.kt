package com.evg.sjl.ast.visitors

import com.evg.sjl.ast.*

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

    fun visit(expression: AssignmentExpression) {
        expression.expression.accept(this)
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

    fun visit(statement: BlockStatement) {
        statement.statements.forEach { it.accept(this) }
    }

    fun visit(statement: WhileStatement) {
        statement.condition.accept(this)
        statement.body.accept(this)
    }

    fun visit(statement: DoWhileStatement) {
        statement.body.accept(this)
        statement.condition.accept(this)
    }

    fun visit(expression: ArrayExpression) {
        expression.length?.accept(this)
        expression.values.forEach { it.accept(this) }
    }

    fun visit(expression: ArrayAccessExpression) {
        expression.array.accept(this)
        expression.key.accept(this)
    }

    fun visit(expression: NewExpression) {
        expression.args.forEach { it.accept(this) }
    }

    fun visit(expression: FieldExpression) {
        expression.target.accept(this)
    }

    fun visit(expression: MethodExpression) {
        expression.target.accept(this)
        expression.args.forEach { it.accept(this) }
    }
}