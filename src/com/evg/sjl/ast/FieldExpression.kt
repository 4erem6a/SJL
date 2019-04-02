package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.InsnNode

class FieldExpression(val target: Expression, val name: String, val type: Type): AssignableExpression {
    override fun compile(context: CompilationContext) {
        val targetType = context.typeInference.getType(target)
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        target.compile(context)
        with(context.il) {
            add(FieldInsnNode(
                    Opcodes.GETFIELD,
                    targetType.name,
                    name,
                    type.jvmType
            ))
        }
    }

    override fun compileAssignment(context: CompilationContext, expression: Expression) {
        val targetType = context.typeInference.getType(target)
        val expressionType = context.typeInference.getType(expression)
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        if (expressionType != type)
            throw InvalidValueTypeException(expressionType)
        target.compile(context)
        expression.compile(context)
        with(context.il) {
            when (type) {
                Primitives.DOUBLE -> add(InsnNode(Opcodes.DUP2_X1))
                else -> add(InsnNode(Opcodes.DUP_X1))
            }
            add(FieldInsnNode(
                    Opcodes.PUTFIELD,
                    targetType.name,
                    name,
                    type.jvmType
            ))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}