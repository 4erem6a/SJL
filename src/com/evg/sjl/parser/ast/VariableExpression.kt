package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Referential
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class VariableExpression(var identifier: String) : AssignableExpression {
    override fun compile(context: CompilationContext) {
        val sym = context.symbolTable[identifier]
                ?: throw VariableUsedWithoutBeingDeclaredException(identifier)
        when (sym.type) {
            Primitives.DOUBLE -> context.il.add(VarInsnNode(Opcodes.DLOAD, sym.index))
            Primitives.INTEGER, Primitives.BOOLEAN -> context.il.add(VarInsnNode(Opcodes.ILOAD, sym.index))
            is Referential -> context.il.add(VarInsnNode(Opcodes.ALOAD, sym.index))
        }
    }

    override fun compileAssignment(context: CompilationContext, expression: Expression) {
        expression.compile(context)
        context.il.add(InsnNode(Opcodes.DUP))
        val symbol = context.symbolTable[identifier]
                ?: context.symbolTable.register(identifier, context.typeInference.getType(expression))
        when (context.typeInference.getType(expression)) {
            Primitives.DOUBLE -> context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            Primitives.INTEGER, Primitives.BOOLEAN -> context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            is Referential -> context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}