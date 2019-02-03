package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor

class ExpressionStatement(var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}