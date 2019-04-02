package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes.GOTO
import jdk.internal.org.objectweb.asm.Opcodes.IFEQ
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode
import jdk.internal.org.objectweb.asm.tree.LabelNode

class WhileStatement(
        var condition: Expression,
        var body: Statement
) : Statement {
    override fun compile(context: CompilationContext) {
        val cType = context.typeInference.getType(condition)
        if (cType != Primitives.BOOLEAN)
            throw InvalidValueTypeException(cType)
        val lCheck = LabelNode()
        val lEnd = LabelNode()
        with(context.il) {
            add(lCheck)
            condition.compile(context)
            add(JumpInsnNode(IFEQ, lEnd))
            body.compile(context)
            add(JumpInsnNode(GOTO, lCheck))
            add(lEnd)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}