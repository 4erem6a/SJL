package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class StaticMethodExpression(val targetType: Type, val name: String, val args: List<Expression>, val type: Type): Expression {
    override fun accept(visitor: Visitor) = visitor.visit(this)

    override fun compile(context: CompilationContext) {
        if (targetType !is JavaClass)
            throw InvalidValueTypeException(targetType)
        args.forEach { it.compile(context) }
        val argumentSignature = args
                .map { context.typeInference.getType(it) }
                .joinToString("") { it.jvmType }
        context.il.add(MethodInsnNode(
                Opcodes.INVOKESTATIC,
                targetType.name,
                name,
                "($argumentSignature)${type.jvmType}",
                false
        ))
    }
}