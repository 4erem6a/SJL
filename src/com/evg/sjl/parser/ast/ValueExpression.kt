package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.*
import jdk.internal.org.objectweb.asm.Opcodes.ICONST_0
import jdk.internal.org.objectweb.asm.Opcodes.ICONST_1
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode

class ValueExpression(var value: Value) : Expression {
    override fun compile(context: CompilationContext) = when (value.type) {
        Types.DOUBLE ->
            context.il.add(LdcInsnNode((value as DoubleValue).value))
        Types.INTEGER ->
            context.il.add(LdcInsnNode((value as IntegerValue).value))
        Types.STRING ->
            context.il.add(LdcInsnNode((value as StringValue).value))
        Types.BOOLEAN ->
            if ((value as BooleanValue).value)
                context.il.add(InsnNode(ICONST_1))
            else context.il.add(InsnNode(ICONST_0))
    }
    override fun accept(visitor: Visitor) = visitor.visit(this)
}