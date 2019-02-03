package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.lib.UnaryOperations
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Label
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
            Types.DOUBLE -> when (operation) {
                UnaryOperations.NEGATION -> context.il.add(InsnNode(DNEG))
                else -> throw InvalidOperandTypesException(operation, type)
            }
            Types.INTEGER -> when (operation) {
                UnaryOperations.NEGATION -> context.il.add(InsnNode(INEG))
                UnaryOperations.BITWISE_NEGATION -> {
                    context.il.add(InsnNode(ICONST_M1))
                    context.il.add(InsnNode(IXOR))
                }
                else -> throw InvalidOperandTypesException(operation, type)
            }
            Types.BOOLEAN -> when (operation) {
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
            else -> throw InvalidOperandTypesException(operation, type)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}