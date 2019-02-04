package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.TypeInferenceFailException
import com.evg.sjl.exceptions.VariableAlreadyDeclaredException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class VariableDefinitionStatement(
        var identifier: String,
        var type: Types? = null,
        var initializer: Expression? = null
) : Statement {
    override fun compile(context: CompilationContext) {
        val iniType = if (initializer != null)
            context.typeInference.getType(initializer!!)
        else null
        if (type != null && iniType != null)
            if (type != iniType)
                initializer = CastExpression(type!!, initializer!!)
        if (type == null && iniType == null)
            throw TypeInferenceFailException()
        val type = type ?: iniType!!
        if (context.symbolTable.getTop(identifier) != null)
            throw VariableAlreadyDeclaredException(identifier)
        val symbol = context.symbolTable.register(identifier, type)
        initializer?.compile(context)
                ?: context.il.add(LdcInsnNode(type.defaultValue))
        when (type) {
            Types.DOUBLE ->
                context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            Types.INTEGER, Types.BOOLEAN ->
                context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            Types.STRING ->
                context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}