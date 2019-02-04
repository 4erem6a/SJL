package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class AssignmentStatement(var identifier: String, var expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val symbol = context.symbolTable[identifier]
                ?: context.symbolTable.register(identifier, context.typeInference.getType(expression))
        when (context.typeInference.getType(expression)) {
            Types.DOUBLE -> context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            Types.INTEGER, Types.BOOLEAN -> context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            Types.STRING -> context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}