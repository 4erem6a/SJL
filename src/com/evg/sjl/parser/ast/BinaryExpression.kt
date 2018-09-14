package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.lib.BinaryOperations
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode

class BinaryExpression(val operation: BinaryOperations,
                       val left: Expression, val right: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        val lt = context.typeInference.getType(left)
        val rt = context.typeInference.getType(right)
        when (lt) {
            Types.NUMBER -> {
                left.compile(context)
                right.compile(context)
                if (rt != Types.NUMBER)
                    throw InvalidOperandTypesException(operation, rt, lt)
                context.il.add(InsnNode(when (operation) {
                    BinaryOperations.ADDITION -> Opcodes.DADD
                    BinaryOperations.SUBTRACTION -> Opcodes.DSUB
                    BinaryOperations.MULTIPLICATION -> Opcodes.DMUL
                    BinaryOperations.DIVISION -> Opcodes.DDIV
                    BinaryOperations.REMAINDER -> Opcodes.DREM
                }))
            }
            Types.STRING -> {
                if (rt == Types.NUMBER || rt == Types.STRING) when (operation) {
                    BinaryOperations.ADDITION -> {
                        context.il.add(TypeInsnNode(Opcodes.NEW, "java/lang/StringBuilder"))
                        context.il.add(InsnNode(Opcodes.DUP))
                        context.il.add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false))
                        left.compile(context)
                        context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
                        right.compile(context)
                        if (rt == Types.NUMBER)
                            context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false))
                        else context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
                        context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false))
                    }
                    else -> throw InvalidOperandTypesException(operation, lt, rt)
                }
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}