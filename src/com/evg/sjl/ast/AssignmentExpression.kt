package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.ast.visitors.Visitor

class AssignmentExpression(var target: AssignableExpression, var expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        target.compileAssignment(context, expression)
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}