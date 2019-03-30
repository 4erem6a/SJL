package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class AssignmentStatement(var identifier: String, var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val symbol = context.symbolTable[identifier]
                ?: context.symbolTable.register(identifier, context.typeInference.getType(expression))
        when (context.typeInference.getType(expression)) {
            Primitives.DOUBLE -> context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            Primitives.INTEGER, Primitives.BOOLEAN -> context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            Primitives.STRING -> context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}