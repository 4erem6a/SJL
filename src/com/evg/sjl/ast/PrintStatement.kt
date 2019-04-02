package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.StringType
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class PrintStatement(var newLine: Boolean, var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        context.il.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        val type = context.typeInference.getType(expression)
        if (type !is Primitives)
            CastExpression(StringType, expression).compile(context)
        else expression.compile(context)
        val signature = when (type) {
            is Primitives, StringType -> "(${type.jvmType})V"
            is JavaClass -> "(Ljava/lang/Object;)V"
            else -> throw InvalidValueTypeException(type)
        }
        context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                if (newLine)
                    "println"
                else "print",
                signature,
                false
        ))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}