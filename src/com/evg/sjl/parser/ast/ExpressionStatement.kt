package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.InsnNode

class ExpressionStatement(var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        val type = context.typeInference.getType(expression)
        expression.compile(context)
        context.il.add(InsnNode(when (type) {
            Types.DOUBLE -> POP2
            else -> POP
        }))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}