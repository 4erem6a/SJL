package com.evg.sjl.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.exceptions.MissingInitializerException
import com.evg.sjl.exceptions.TypeInferenceFailException
import com.evg.sjl.exceptions.VariableAlreadyDeclaredException
import com.evg.sjl.ast.visitors.Visitor
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Referential
import com.evg.sjl.values.StringType
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.VarInsnNode

class VariableDefinitionStatement(
        var identifier: String,
        var type: Type? = null,
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
                ?: if (type is Primitives)
                    ValueExpression(type.defaultValue).compile(context)
                else if (type is StringType)
                    ValueExpression(StringType.defaultValue).compile(context)
                else throw MissingInitializerException(identifier)
        when (type) {
            Primitives.DOUBLE ->
                context.il.add(VarInsnNode(Opcodes.DSTORE, symbol.index))
            Primitives.INTEGER, Primitives.BOOLEAN ->
                context.il.add(VarInsnNode(Opcodes.ISTORE, symbol.index))
            is Referential ->
                context.il.add(VarInsnNode(Opcodes.ASTORE, symbol.index))
            else -> throw InvalidValueTypeException(type)
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}