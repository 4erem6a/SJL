package com.evg.sjl.codegen.visitors

import com.evg.sjl.codegen.SymbolTable
import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*
import com.evg.sjl.parser.visitors.Visitor
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*
import com.evg.sjl.codegen.ByteCodeGenerator.Companion.generatedClassName
import com.evg.sjl.codegen.ByteCodeGenerator.Companion.scannerFieldName

class CodegenVisitor : Visitor {
    private val st = SymbolTable()
    private val il = InsnList()

    val instructions: InsnList
        get() = il

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    override fun visit(statement: PrintStatement) {
        il.add(FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        statement.expression.accept(this)
        if (!statement.charMode)
            il.add(MethodInsnNode(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false))
        else il.add(InsnNode(D2I))
        il.add(MethodInsnNode(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                if (statement.newLine)
                    "println"
                else "print",
                if (statement.charMode)
                    "(C)V"
                else "(Ljava/lang/Object;)V",
                false
        ))
    }

    override fun visit(statement: UnionStatement) {
        for (stmt in statement.statements)
            stmt.accept(this)
    }

    override fun visit(expression: NumberExpression) {
        il.add(LdcInsnNode(java.lang.Double(expression.number)))
    }

    override fun visit(expression: BinaryExpression) {
        expression.left.accept(this)
        expression.right.accept(this)
        il.add(InsnNode(when (expression.operation) {
            ADDITION -> DADD
            SUBTRACTION -> DSUB
            MULTIPLICATION -> DMUL
            DIVISION -> DDIV
            REMAINDER -> DREM
        }))
    }

    override fun visit(expression: UnaryExpression) {
        expression.expression.accept(this)
        if (expression.expression == NEGATION)
            il.add(InsnNode(DNEG))
    }

    override fun visit(expression: AssignmentStatement) {
        expression.expression.accept(this)
        if (expression.identifier in st.symbols) {
            val index = st[expression.identifier]
            if (index != null)
                il.add(VarInsnNode(DSTORE, index))
        } else {
            val index = st.register(expression.identifier)
            il.add(VarInsnNode(DSTORE, index))
        }
    }

    override fun visit(expression: VariableExpression) {
        val index = st[expression.identifier]
                ?: throw VariableUsedWithoutBeingDeclaredException(expression.identifier)
        il.add(VarInsnNode(DLOAD, index))
    }

    override fun visit(expression: InputExpression) {
        if (expression.charMode) {
            il.add(FieldInsnNode(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"))
            il.add(MethodInsnNode(INVOKEVIRTUAL, "java/io/InputStream", "read", "()I", false))
            il.add(InsnNode(I2D))
        } else {
            il.add(VarInsnNode(ALOAD, 0))
            il.add(FieldInsnNode(GETFIELD, generatedClassName, scannerFieldName, "Ljava/util/Scanner;"))
            il.add(MethodInsnNode(INVOKEVIRTUAL, "java/util/Scanner", "nextDouble", "()D", false))
        }
    }
}