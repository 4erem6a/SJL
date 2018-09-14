package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.ByteCodeGenerator
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class InputExpression(val type: Types, val charMode: Boolean = false) : Expression {
    override fun compile(context: CompilationContext) {
        if (charMode) {
            context.il.add(FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"))
            context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/InputStream", "read", "()I", false))
            context.il.add(InsnNode(Opcodes.I2D))
        } else {
            context.il.add(VarInsnNode(Opcodes.ALOAD, 0))
            context.il.add(FieldInsnNode(Opcodes.GETFIELD, ByteCodeGenerator.generatedClassName, ByteCodeGenerator.scannerFieldName, "Ljava/util/Scanner;"))
            context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()D", false))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}