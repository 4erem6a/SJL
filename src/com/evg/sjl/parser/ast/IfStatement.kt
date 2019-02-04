package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeExveption
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode
import jdk.internal.org.objectweb.asm.tree.LabelNode

class IfStatement(
        var condition: Expression,
        var ifStatement: Statement,
        var elseStatement: Statement? = null
) : Statement {
    override fun compile(context: CompilationContext) {
        val type = context.typeInference.getType(condition)
        if (type != Types.BOOLEAN)
            throw InvalidValueTypeExveption(type)
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

    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}