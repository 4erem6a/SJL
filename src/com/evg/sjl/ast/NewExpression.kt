package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode

class NewExpression(val type: Type, val args: List<Expression> = listOf()) : Expression {
    override fun compile(context: CompilationContext) {
        if (type !is JavaClass)
            throw InvalidValueTypeException(type)
        with(context) {
            il.add(TypeInsnNode(Opcodes.NEW, type.name))
            il.add(InsnNode(Opcodes.DUP))
            val argumentSignature = args
                    .map { typeInference.getType(it) }
                    .joinToString { it.jvmType }
            args.forEach { it.compile(context) }
            il.add(MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    type.name,
                    "<init>",
                    "($argumentSignature)V",
                    false
            ))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}