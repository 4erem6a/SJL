package com.evg.sjl.codegen

import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.parser.ast.*
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types

class TypeInferenceProvider(val symbolTable: SymbolTable) {
    fun getType(expression: Expression): Types {
        val visitor = TypeInferenceVisitor(symbolTable)
        expression.accept(visitor)
        return visitor.type
    }
}

class TypeInferenceVisitor(val st: SymbolTable) : Visitor {
    lateinit var type: Types

    override fun visit(expression: ValueExpression) {
        type = expression.value.type
    }

    override fun visit(expression: BinaryExpression) {
        expression.left.accept(this)
    }

    override fun visit(expression: UnaryExpression) {
        expression.expression.accept(this)
    }

    override fun visit(expression: VariableExpression) {
        val symbol = st[expression.identifier]
        type = symbol?.type ?: throw VariableUsedWithoutBeingDeclaredException(expression.identifier)
    }

    override fun visit(expression: InputExpression) {
        type = expression.type
    }

    override fun visit(expression: CastExpression) {
        type = expression.type
    }
}