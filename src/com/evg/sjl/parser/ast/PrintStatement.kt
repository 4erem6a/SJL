package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class PrintStatement(var newLine: Boolean, var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        context.il.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        expression.compile(context)
        val signature = when (context.typeInference.getType(expression)) {
            Types.DOUBLE -> "(D)V"
            Types.INTEGER -> "(I)V"
            Types.STRING -> "(Ljava/lang/String;)V"
            Types.BOOLEAN -> "(Z)V"
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