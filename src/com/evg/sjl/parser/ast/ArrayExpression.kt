package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidValueTypeException
import com.evg.sjl.exceptions.TypeInferenceFailException
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.JavaClass
import com.evg.sjl.values.Primitives
import com.evg.sjl.values.Referential
import com.evg.sjl.values.Type
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.InsnNode
import jdk.internal.org.objectweb.asm.tree.IntInsnNode
import jdk.internal.org.objectweb.asm.tree.LdcInsnNode
import jdk.internal.org.objectweb.asm.tree.TypeInsnNode

class ArrayExpression(val type: Type?, val length: Expression?, val values: List<Expression> = listOf()) : Expression {
    override fun compile(context: CompilationContext) {
        val type = type ?: if (values.isNotEmpty())
            context.typeInference.getType(values.first())
        else throw TypeInferenceFailException()
        with(context.il) {
            if (length == null)
                add(LdcInsnNode(values.size))
            else {
                val lengthType = context.typeInference.getType(length)
                if (lengthType != Primitives.INTEGER)
                    throw InvalidValueTypeException(lengthType)
                length.compile(context)
            }
            when (type) {
                is Primitives -> add(IntInsnNode(NEWARRAY, type.jvmCode))
                is JavaClass -> add(TypeInsnNode(ANEWARRAY, type.name))
                is Referential -> add(TypeInsnNode(ANEWARRAY, type.jvmType))
                else -> throw InvalidValueTypeException(type)
            }
            values.forEachIndexed { index, expression ->
                val expressionType = context.typeInference.getType(expression)
                if (expressionType != type)
                    throw InvalidValueTypeException(expressionType)
                add(InsnNode(DUP))
                add(LdcInsnNode(index))
                expression.compile(context)
                when (type) {
                    Primitives.INTEGER, Primitives.BOOLEAN -> add(InsnNode(IASTORE))
                    Primitives.DOUBLE -> add(InsnNode(DASTORE))
                    is Referential -> add(InsnNode(AASTORE))
                }
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}