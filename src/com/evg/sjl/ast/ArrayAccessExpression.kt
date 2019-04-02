package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.ArrayType
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Referential
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.InsnNode

class ArrayAccessExpression(val array: Expression, val key: Expression) : AssignableExpression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }

    override fun compile(context: CompilationContext) {
        val arrayType = context.typeInference.getType(array)
        val keyType = context.typeInference.getType(key)
        if (arrayType !is ArrayType)
            throw InvalidValueTypeException(arrayType)
        if (keyType != Primitives.INTEGER)
            throw InvalidValueTypeException(keyType)
        array.compile(context)
        key.compile(context)
        when (arrayType.type) {
            Primitives.INTEGER, Primitives.BOOLEAN ->
                context.il.add(InsnNode(IALOAD))
            Primitives.DOUBLE ->
                context.il.add(InsnNode(DALOAD))
            is Referential ->
                context.il.add(InsnNode(AALOAD))
            else -> throw InvalidValueTypeException(arrayType.type)
        }
    }

    override fun compileAssignment(context: CompilationContext, expression: Expression) {
        val arrayType = context.typeInference.getType(array)
        val keyType = context.typeInference.getType(key)
        val expressionType = context.typeInference.getType(expression)
        if (arrayType !is ArrayType)
            throw InvalidValueTypeException(arrayType)
        if (keyType != Primitives.INTEGER)
            throw InvalidValueTypeException(keyType)
        if (arrayType.type != expressionType)
            throw InvalidValueTypeException(expressionType)
        array.compile(context)
        key.compile(context)
        expression.compile(context)
        when (arrayType.type) {
            Primitives.DOUBLE ->
                context.il.add(InsnNode(DUP2_X2))
            else -> context.il.add(InsnNode(DUP_X2))
        }
        when (arrayType.type) {
            Primitives.INTEGER, Primitives.BOOLEAN ->
                context.il.add(InsnNode(IASTORE))
            Primitives.DOUBLE ->
                context.il.add(InsnNode(DASTORE))
            is Referential ->
                context.il.add(InsnNode(AASTORE))
            else -> throw InvalidValueTypeException(arrayType.type)
        }
    }
}