package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor

class UnionStatement(var statements: List<Statement>) : Statement {
    override fun compile(context: CompilationContext) {
        for (stmt in statements)
            stmt.compile(context)
    }
    override fun accept(visitor: Visitor) = visitor.visit(this)
}