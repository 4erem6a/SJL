package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidCastException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.ArrayType
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.StringType
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
                is StringType -> context.il.add(MethodInsnNode(
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
                is StringType ->
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
            Primitives.BOOLEAN -> when (type) {
                Primitives.BOOLEAN, Primitives.INTEGER -> return
                Primitives.DOUBLE -> context.il.add(InsnNode(I2D))
                is StringType -> context.il.add(MethodInsnNode(
                        INVOKESTATIC,
                        "java/lang/String",
                        "valueOf",
                        "(Z)Ljava/lang/String;",
                        false
                ))
            }
            is StringType -> if (type !is StringType)
                throw InvalidCastException(tFrom, type)
            is ArrayType -> if (type is StringType) {
                if (tFrom.type is Primitives) context.il.add(
                        MethodInsnNode(
                                INVOKESTATIC,
                                "java/util/Arrays",
                                "toString",
                                "(${tFrom.jvmType})${type.jvmType}",
                                false
                        )
                ) else context.il.add(
                        MethodInsnNode(
                                INVOKESTATIC,
                                "java/util/Arrays",
                                "deepToString",
                                "([Ljava/lang/Object;)${type.jvmType}",
                                false
                        )
                )
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