package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidCastException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class CastExpression(val type: Types, val expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val tFrom = context.typeInference.getType(expression)
        when (tFrom) {
            Types.INTEGER -> when (type) {
                Types.INTEGER -> return
                Types.DOUBLE -> context.il.add(InsnNode(I2D))
                Types.STRING ->
                        context.il.add(MethodInsnNode(INVOKESTATIC,
                                "java/lang/String",
                                "valueOf",
                                "(I)Ljava/lang/String;",
                                false))
            }
            Types.DOUBLE -> when (type) {
                Types.DOUBLE -> return
                Types.INTEGER -> context.il.add(InsnNode(D2I))
                Types.STRING ->
                    context.il.add(MethodInsnNode(INVOKESTATIC,
                            "java/lang/String",
                            "valueOf",
                            "(D)Ljava/lang/String;",
                            false))
            }
            Types.STRING -> if (tFrom != Types.STRING)
                throw InvalidCastException(tFrom, type)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}