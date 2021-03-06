package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes.GOTO
import jdk.internal.org.objectweb.asm.Opcodes.IFEQ
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode
import jdk.internal.org.objectweb.asm.tree.LabelNode

class IfStatement(
        var condition: Expression,
        var ifStatement: Statement,
        var elseStatement: Statement? = null
) : Statement {
    override fun compile(context: CompilationContext) {
        val type = context.typeInference.getType(condition)
        if (type != Primitives.BOOLEAN)
            throw InvalidValueTypeException(type)
        condition.compile(context)
        with(context.il) {
            val lFalse = LabelNode()
            val lEnd = LabelNode()
            if (elseStatement != null)
                add(JumpInsnNode(IFEQ, lFalse))
            else add(JumpInsnNode(IFEQ, lEnd))
            ifStatement.compile(context)
            if (elseStatement != null) {
                add(JumpInsnNode(GOTO, lEnd))
                add(lFalse)
                elseStatement?.compile(context)
            }
            add(lEnd)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}