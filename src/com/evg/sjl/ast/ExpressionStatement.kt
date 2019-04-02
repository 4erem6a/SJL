package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.VoidType
import jdk.internal.org.objectweb.asm.Opcodes.POP
import jdk.internal.org.objectweb.asm.Opcodes.POP2
import jdk.internal.org.objectweb.asm.tree.InsnNode

class ExpressionStatement(var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        val type = context.typeInference.getType(expression)
        expression.compile(context)
        when (type) {
            Primitives.DOUBLE -> context.il.add(InsnNode(POP2))
            !is VoidType -> context.il.add(InsnNode(POP))
        }

    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}