package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.NumberValue
import com.evg.sjl.values.StringValue
import com.evg.sjl.values.Value
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode

class ValueExpression(val value: Value) : Expression {
    override fun compile(context: CompilationContext) {
        when (value) {
            is NumberValue ->
                context.il.add(LdcInsnNode(value.value))
            is StringValue ->
                context.il.add(LdcInsnNode(value.value))
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}