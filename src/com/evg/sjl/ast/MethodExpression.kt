package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.JavaInterface
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class MethodExpression(val target: Expression, val name: String, val args: List<Expression>, val type: Type): Expression {
    override fun compile(context: CompilationContext) {
        val targetType = context.typeInference.getType(target)
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        target.compile(context)
        args.forEach { it.compile(context) }
        val argumentSignature = args
                .map { context.typeInference.getType(it) }
                .joinToString("") { it.jvmType }
        context.il.add(MethodInsnNode(
                if (targetType is JavaInterface)
                    Opcodes.INVOKEINTERFACE
                else Opcodes.INVOKEVIRTUAL,
                targetType.name,
                name,
                "($argumentSignature)${type.jvmType}",
                false
        ))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}