package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidCastException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*

class CastExpression(var type: Types, var expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val tFrom = context.typeInference.getType(expression)
        when (tFrom) {
            Types.INTEGER -> when (type) {
                Types.INTEGER -> return
                Types.DOUBLE -> context.il.add(InsnNode(I2D))
                Types.STRING -> context.il.add(MethodInsnNode(
                        INVOKESTATIC,
                        "java/lang/String",
                        "valueOf",
                        "(I)Ljava/lang/String;",
                        false
                ))
                Types.BOOLEAN -> context.il.i2boolean()
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
                Types.BOOLEAN -> with(context.il) {
                    add(InsnNode(D2I))
                    i2boolean()
                }
            }
            Types.STRING -> if (tFrom != Types.STRING)
                throw InvalidCastException(tFrom, type)
            Types.BOOLEAN -> when (type) {
                Types.BOOLEAN, Types.INTEGER -> return
                Types.DOUBLE -> context.il.add(InsnNode(I2D))
                Types.STRING -> context.il.add(MethodInsnNode(
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