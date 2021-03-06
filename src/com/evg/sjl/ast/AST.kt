package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.ast.visitors.PrintVisitor
import com.evg.sjl.ast.visitors.Visitor

interface Node {
    fun accept(visitor: Visitor)
    fun compile(context: CompilationContext)

    fun stringify(): String {
        val printVisitor = PrintVisitor()
        this.accept(printVisitor)
        return printVisitor.toString()
    }
}

interface Statement : Node

interface Expression : Node

interface AssignableExpression : Expression {
    fun compileAssignment(context: CompilationContext, expression: Expression)
}