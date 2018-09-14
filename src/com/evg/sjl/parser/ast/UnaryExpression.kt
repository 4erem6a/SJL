package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.lib.UnaryOperations
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes
import jdk.internal.org.objectweb.asm.tree.InsnNode

class UnaryExpression(val operation: UnaryOperations,
                      val expression: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        expression.compile(context)
        val type = context.typeInference.getType(expression)
        if (type != Types.NUMBER)
            throw InvalidOperandTypesException(operation, type)
        if (expression == UnaryOperations.NEGATION)
            context.il.add(InsnNode(Opcodes.DNEG))
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}