package com.evg.sjl.ast

import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.codegen.BytecodeGenerator
import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.StringType
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.FieldInsnNode
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class InputExpression(val type: Type) : Expression {
    override fun compile(context: CompilationContext) {
        context.il.add(VarInsnNode(Opcodes.ALOAD, 0))
        context.il.add(FieldInsnNode(Opcodes.GETFIELD, BytecodeGenerator.currentClassName, BytecodeGenerator.scannerFieldName, "Ljava/util/Scanner;"))
        when (type) {
            Primitives.DOUBLE ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()D", false))
            Primitives.INTEGER ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false))
            Primitives.BOOLEAN ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextBoolean", "()Z", false))
            is StringType ->
                context.il.add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}