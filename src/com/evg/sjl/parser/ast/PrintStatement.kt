package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class PrintStatement(var newLine: Boolean, var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        context.il.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        expression.compile(context)
        val signature = when (context.typeInference.getType(expression)) {
            Primitives.DOUBLE -> "(D)V"
            Primitives.INTEGER -> "(I)V"
            Primitives.STRING -> "(Ljava/lang/String;)V"
            Primitives.BOOLEAN -> "(Z)V"
            else -> "(Ljava/lang/Object;)V"
        }
        context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                if (newLine)
                    "println"
                else "print",
                signature,
                false))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}