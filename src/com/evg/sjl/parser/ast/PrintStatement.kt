package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode

class PrintStatement(val newLine: Boolean, val charMode: Boolean, val expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        context.il.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        expression.compile(context)
        if (context.typeInference.getType(expression) == Types.STRING) {
            context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    if (newLine)
                        "println"
                    else "print" ,
                    "(Ljava/lang/String;)V",
                    false))
            return
        }
        if (!charMode)
            context.il.add(MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false))
        else context.il.add(InsnNode(Opcodes.D2I))
        context.il.add(MethodInsnNode(
                Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                if (newLine)
                    "println"
                else "print",
                if (charMode)
                    "(C)V"
                else "(Ljava/lang/Object;)V",
                false
        ))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}