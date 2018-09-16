package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.*
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode

class ValueExpression(val value: Value) : Expression {
    override fun compile(context: CompilationContext) {
        when (value.type) {
            Types.DOUBLE ->
                context.il.add(LdcInsnNode((value as DoubleValue).value))
            Types.INTEGER ->
                context.il.add(LdcInsnNode((value as IntegerValue).value))
            Types.STRING ->
                context.il.add(LdcInsnNode((value as StringValue).value))
        }
    }
    override fun accept(visitor: Visitor) = visitor.visit(this)
}