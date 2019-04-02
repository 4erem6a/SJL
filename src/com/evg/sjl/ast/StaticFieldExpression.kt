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

class StaticFieldExpression(val targetType: Type, val name: String, val type: Type): AssignableExpression {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun compile(context: CompilationContext) {
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        context.il.add(FieldInsnNode(
                Opcodes.GETSTATIC,
                targetType.name,
                name,
                type.jvmType
        ))
    }

    override fun compileAssignment(context: CompilationContext, expression: Expression) {
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        val expressionType = context.typeInference.getType(expression)
        if (expressionType != type)
            throw InvalidValueTypeException(expressionType)
        expression.compile(context)
        with(context.il) {
            when (type) {
                Primitives.DOUBLE -> add(InsnNode(Opcodes.DUP2))
                else -> add(InsnNode(Opcodes.DUP))
            }
            add(FieldInsnNode(
                    Opcodes.PUTSTATIC,
                    targetType.name,
                    name,
                    type.jvmType
            ))
        }
    }
}