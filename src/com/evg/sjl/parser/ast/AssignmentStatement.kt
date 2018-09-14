package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class AssignmentStatement(val identifier: String, val expression: Expression) : Statement {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        if (identifier in context.symbolTable.symbols) {
            val symbol = context.symbolTable[identifier]
            if (symbol != null) when (context.typeInference.getType(expression)) {
                Types.NUMBER -> context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
                Types.STRING -> context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
            }
        } else {
            val symbol = context.symbolTable.register(identifier, context.typeInference.getType(expression))
            when (symbol.type) {
                Types.NUMBER -> context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
                Types.STRING -> context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}