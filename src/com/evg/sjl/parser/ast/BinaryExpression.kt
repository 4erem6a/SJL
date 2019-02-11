package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.lib.BinaryOperations
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.Types
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*

class BinaryExpression(var operation: BinaryOperations,
                       var left: Expression, var right: Expression) : Expression {
    override fun compile(context: CompilationContext) {
        val lt = context.typeInference.getType(left)
        val rType = context.typeInference.getType(right)
        if (lt != rType && rType != Types.STRING)
            right = CastExpression(lt, right)
        val rt = context.typeInference.getType(right)
        when (lt) {
            Types.DOUBLE -> {
                if (rt != Types.DOUBLE)
                    throw InvalidOperandTypesException(operation, lt, rType)
                when (operation) {
                    BinaryOperations.ADDITION -> dAdd(context)
                    BinaryOperations.SUBTRACTION -> dSub(context)
                    BinaryOperations.MULTIPLICATION -> dMult(context)
                    BinaryOperations.DIVISION -> dDiv(context)
                    BinaryOperations.REMAINDER -> dRem(context)
                    BinaryOperations.LOWER_THAN -> dLT(context)
                    BinaryOperations.GREATER_THAN -> dGT(context)
                    BinaryOperations.EQUALS_OR_LOWER_THAN -> dLE(context)
                    BinaryOperations.EQUALS_OR_GREATER_THAN -> dGE(context)
                    BinaryOperations.EQUALS -> dEquals(context)
                    BinaryOperations.NOT_EQUALS -> dNotEquals(context)
                    else -> throw InvalidOperandTypesException(operation, lt, rType)
                }
            }
            Types.INTEGER -> {
                if (rt != Types.INTEGER)
                    throw InvalidOperandTypesException(operation, lt, rType)
                when (operation) {
                    BinaryOperations.ADDITION -> iAdd(context)
                    BinaryOperations.SUBTRACTION -> iSub(context)
                    BinaryOperations.MULTIPLICATION -> iMult(context)
                    BinaryOperations.DIVISION -> iDiv(context)
                    BinaryOperations.REMAINDER -> iRem(context)
                    BinaryOperations.LEFT_SHIFT -> iShl(context)
                    BinaryOperations.RIGHT_SHIFT -> iShr(context)
                    BinaryOperations.UNSIGNED_RIGHT_SHIFT -> iUshr(context)
                    BinaryOperations.BITWISE_AND -> iAnd(context)
                    BinaryOperations.BITWISE_XOR -> iXor(context)
                    BinaryOperations.BITWISE_OR -> iOr(context)
                    BinaryOperations.EQUALS -> iEquals(context)
                    BinaryOperations.NOT_EQUALS -> iNotEquals(context)
                    BinaryOperations.EQUALS_OR_GREATER_THAN -> iGE(context)
                    BinaryOperations.LOWER_THAN -> iLT(context)
                    BinaryOperations.GREATER_THAN -> iGT(context)
                    BinaryOperations.EQUALS_OR_LOWER_THAN -> iLE(context)
                    else -> throw InvalidOperandTypesException(operation, lt, rType)
                }
            }
            Types.BOOLEAN -> {
                if (rt != Types.BOOLEAN)
                    throw InvalidOperandTypesException(operation, lt, rType)
                when (operation) {
                    BinaryOperations.BOOLEAN_AND -> bAnd(context)
                    BinaryOperations.BOOLEAN_OR -> bOr(context)
                    BinaryOperations.BOOLEAN_XOR -> iXor(context)
                    BinaryOperations.EQUALS -> iEquals(context)
                    BinaryOperations.NOT_EQUALS -> iNotEquals(context)
                    else -> throw InvalidOperandTypesException(operation, lt, rType)
                }
            }
            Types.STRING -> {
                when (operation) {
                    BinaryOperations.ADDITION -> sAdd(context, rt)
                    BinaryOperations.EQUALS -> sEquals(context)
                    BinaryOperations.NOT_EQUALS -> sNotEquals(context)
                    else -> throw InvalidOperandTypesException(operation, lt, rType)
                }
            }
        }
    }

    override fun accept(visitor: Visitor) = visitor.visit(this)
}

private fun BinaryExpression.iGE(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPLT, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iLE(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPGT, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iGT(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPLE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iLT(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPGE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.dGE(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPG))
    context.il.add(JumpInsnNode(IFLT, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.dLE(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPL))
    context.il.add(JumpInsnNode(IFGT, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.dGT(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPG))
    context.il.add(JumpInsnNode(IFLE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.dLT(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPL))
    context.il.add(JumpInsnNode(IFGE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.sAdd(context: CompilationContext, rt: Types) {
    context.il.add(TypeInsnNode(NEW, "java/lang/StringBuilder"))
    context.il.add(InsnNode(DUP))
    context.il.add(MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false))
    left.compile(context)
    context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
    right.compile(context)
    when (rt) {
        Types.DOUBLE -> context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false))
        Types.INTEGER -> context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false))
        Types.STRING -> context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
        Types.BOOLEAN -> context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false))
    }
    context.il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false))
}

private fun BinaryExpression.sEquals(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(
            MethodInsnNode(
                    INVOKEVIRTUAL,
                    "java/lang/String",
                    "equals",
                    "(Ljava/lang/Object;)Z",
                    false
            )
    )
}

private fun BinaryExpression.sNotEquals(context: CompilationContext) {
    sEquals(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    with(context.il) {
        add(JumpInsnNode(IFNE, lFalse))
        add(InsnNode(ICONST_1))
        add(JumpInsnNode(GOTO, lEnd))
        add(lFalse)
        add(InsnNode(ICONST_0))
        add(lEnd)
    }
}

private fun BinaryExpression.dRem(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(DREM))
}

private fun BinaryExpression.dDiv(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(DDIV))
}

private fun BinaryExpression.dMult(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(DMUL))
}

private fun BinaryExpression.dSub(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(DSUB))
}

private fun BinaryExpression.dAdd(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(DADD))
}

private fun BinaryExpression.bOr(context: CompilationContext) {
    left.compile(context)
    val lTrue = LabelNode()
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IFNE, lTrue))
    right.compile(context)
    context.il.add(JumpInsnNode(IFEQ, lFalse))
    context.il.add(lTrue)
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.bAnd(context: CompilationContext) {
    left.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IFEQ, lFalse))
    right.compile(context)
    context.il.add(JumpInsnNode(IFEQ, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iNotEquals(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPEQ, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iEquals(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(JumpInsnNode(IF_ICMPNE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.dNotEquals(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPL))
    context.il.add(JumpInsnNode(IFEQ, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}


private fun BinaryExpression.dEquals(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    val lFalse = LabelNode()
    val lEnd = LabelNode()
    context.il.add(InsnNode(DCMPL))
    context.il.add(JumpInsnNode(IFNE, lFalse))
    context.il.add(InsnNode(ICONST_1))
    context.il.add(JumpInsnNode(GOTO, lEnd))
    context.il.add(lFalse)
    context.il.add(InsnNode(ICONST_0))
    context.il.add(lEnd)
}

private fun BinaryExpression.iOr(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IOR))
}

private fun BinaryExpression.iXor(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IXOR))
}

private fun BinaryExpression.iAnd(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IAND))
}

private fun BinaryExpression.iUshr(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IUSHR))
}

private fun BinaryExpression.iShr(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(ISHR))
}

private fun BinaryExpression.iShl(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(ISHL))
}

private fun BinaryExpression.iRem(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IREM))
}

private fun BinaryExpression.iDiv(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IDIV))
}

private fun BinaryExpression.iMult(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IMUL))
}

private fun BinaryExpression.iSub(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(ISUB))
}

private fun BinaryExpression.iAdd(context: CompilationContext) {
    left.compile(context)
    right.compile(context)
    context.il.add(InsnNode(IADD))
}