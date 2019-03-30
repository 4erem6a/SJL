package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class VariableExpression(var identifier: String) : Expression {
    override fun compile(context: CompilationContext) {
        val sym = context.symbolTable[identifier]
                ?: throw VariableUsedWithoutBeingDeclaredException(identifier)
        when (sym.type) {
            Primitives.DOUBLE -> context.il.add(VarInsnNode(Opcodes.DLOAD, sym.index))
            Primitives.INTEGER, Primitives.BOOLEAN -> context.il.add(VarInsnNode(Opcodes.ILOAD, sym.index))
            Primitives.STRING -> context.il.add(VarInsnNode(Opcodes.ALOAD, sym.index))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}