package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode
import jdk.internal.org.objectweb.asm.tree.LabelNode

class DoWhileStatement(
        var body: Statement,
        var condition: Expression
) : Statement {
    override fun compile(context: CompilationContext) {
        val cType = context.typeInference.getType(condition)
        if (cType != Primitives.BOOLEAN)
            throw InvalidValueTypeException(cType)
        val lLoop = LabelNode()
        with(context.il) {
            add(lLoop)
            condition.compile(context)
            body.compile(context)
            add(JumpInsnNode(Opcodes.IFNE, lLoop))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}