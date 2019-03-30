package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidCastException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*

class CastExpression(var type: Type, var expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val tFrom = context.typeInference.getType(expression)
        when (tFrom) {
            Primitives.INTEGER -> when (type) {
                Primitives.INTEGER -> return
                Primitives.DOUBLE -> context.il.add(InsnNode(I2D))
                Primitives.STRING -> context.il.add(MethodInsnNode(
                        INVOKESTATIC,
                        "java/lang/String",
                        "valueOf",
                        "(I)Ljava/lang/String;",
                        false
                ))
                Primitives.BOOLEAN -> context.il.i2boolean()
            }
            Primitives.DOUBLE -> when (type) {
                Primitives.DOUBLE -> return
                Primitives.INTEGER -> context.il.add(InsnNode(D2I))
                Primitives.STRING ->
                    context.il.add(MethodInsnNode(INVOKESTATIC,
                            "java/lang/String",
                            "valueOf",
                            "(D)Ljava/lang/String;",
                            false))
                Primitives.BOOLEAN -> with(context.il) {
                    add(InsnNode(D2I))
                    i2boolean()
                }
            }
            Primitives.STRING -> if (tFrom != Primitives.STRING)
                throw InvalidCastException(tFrom, type)
            Primitives.BOOLEAN -> when (type) {
                Primitives.BOOLEAN, Primitives.INTEGER -> return
                Primitives.DOUBLE -> context.il.add(InsnNode(I2D))
                Primitives.STRING -> context.il.add(MethodInsnNode(
                        INVOKESTATIC,
                        "java/lang/String",
                        "valueOf",
                        "(Z)Ljava/lang/String;",
                        false
                ))
            }
        }
    }

    private fun InsnList.i2boolean() {
        val lFalse = LabelNode()
        val lEnd = LabelNode()
        add(JumpInsnNode(IFNE, lFalse))
        add(InsnNode(ICONST_1))
        add(JumpInsnNode(GOTO, lEnd))
        add(lFalse)
        add(InsnNode(ICONST_0))
        add(lEnd)
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}