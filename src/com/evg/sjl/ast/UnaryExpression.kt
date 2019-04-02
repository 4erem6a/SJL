package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.lib.UnaryOperations
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.ArrayType
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode
import jdk.internal.org.objectweb.asm.tree.LabelNode

class UnaryExpression(var operation: UnaryOperations,
                      var expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val type = context.typeInference.getType(expression)
        when (type) {
            Primitives.DOUBLE -> when (operation) {
                UnaryOperations.NEGATION -> context.il.add(InsnNode(DNEG))
                else -> throw InvalidOperandTypesException(operation, type)
            }
            Primitives.INTEGER -> when (operation) {
                UnaryOperations.NEGATION -> context.il.add(InsnNode(INEG))
                UnaryOperations.BITWISE_NEGATION -> {
                    context.il.add(InsnNode(ICONST_M1))
                    context.il.add(InsnNode(IXOR))
                }
                else -> throw InvalidOperandTypesException(operation, type)
            }
            Primitives.BOOLEAN -> when (operation) {
                UnaryOperations.BOOLEAN_NEGATION -> {
                    val lFalse = LabelNode()
                    val lEnd = LabelNode()
                    with(context.il) {
                        add(JumpInsnNode(IFNE, lFalse))
                        add(InsnNode(ICONST_1))
                        add(JumpInsnNode(GOTO, lEnd))
                        add(lFalse)
                        add(InsnNode(ICONST_0))
                        add(lEnd)
                    }
                }
                else -> throw InvalidOperandTypesException(operation, type)
            }
            is ArrayType -> when (operation) {
                UnaryOperations.ARRAY_LENGTH -> {
                    context.il.add(InsnNode(ARRAYLENGTH))
                }
                else -> throw InvalidOperandTypesException(operation, type)
            }
            else -> throw InvalidOperandTypesException(operation, type)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}