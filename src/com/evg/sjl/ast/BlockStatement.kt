package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.ast.visitors.Visitor

class BlockStatement(var statements: List<Statement>) : Statement {
    override fun compile(context: CompilationContext) {
        context.symbolTable.upScope()
        for (stmt in statements)
            stmt.compile(context)
        context.symbolTable.downScope()
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}