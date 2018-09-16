package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class VariableDefinitionStatement(val identifier: String, val type: Types) : Statement {
    override fun compile(context: CompilationContext) {
        val symbol = context.symbolTable.register(identifier, type)
        when (type) {
            Types.DOUBLE -> {
                context.il.add(LdcInsnNode(type.defaultValue))
                context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            }
            Types.INTEGER -> {
                context.il.add(LdcInsnNode(type.defaultValue))
                context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            }
            Types.STRING -> {
                context.il.add(LdcInsnNode(type.defaultValue))
                context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}