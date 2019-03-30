package com.evg.sjl.codegen

import com.evg.sjl.exceptions.TypeInferenceFailException
import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.parser.ast.*
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Type

class TypeInferenceProvider(val symbolTable: SymbolTable) {
    fun getType(expression: Expression): Type {
        val visitor = TypeInferenceVisitor(symbolTable)
        expression.accept(visitor)
        return visitor.type
                ?: throw TypeInferenceFailException()
    }
}

class TypeInferenceVisitor(val st: SymbolTable) : Visitor {
    var type: Type? = null

    override fun visit(expression: ValueExpression) {
        type = expression.value.type
    }

    override fun visit(expression: BinaryExpression) {
        if (expression.operation in
                arrayOf(EQUALS,
                        NOT_EQUALS,
                        EQUALS_OR_GREATER_THAN,
                        EQUALS_OR_LOWER_THAN,
                        LOWER_THAN,
                        GREATER_THAN))
            type = Primitives.BOOLEAN
        else expression.left.accept(this)
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