package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.ByteCodeGenerator
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class InputExpression(val type: Types) : Expression {
    override fun compile(context: CompilationContext) {
        context.il.add(VarInsnNode(Opcodes.ALOAD, 0))
        context.il.add(FieldInsnNode(Opcodes.GETFIELD, ByteCodeGenerator.generatedClassName, ByteCodeGenerator.scannerFieldName, "Ljava/util/Scanner;"))
        when (type) {
            Types.DOUBLE ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()D", false))
            Types.INTEGER ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false))
            Types.STRING ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false))
            Types.BOOLEAN ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextBoolean", "()Z", false))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}