package com.evg.sjl.codegen.visitors

import com.evg.sjl.codegen.ByteCodeGenerator.Companion.generatedClassName
import com.evg.sjl.codegen.ByteCodeGenerator.Companion.scannerFieldName
import com.evg.sjl.codegen.SymbolTable
import com.evg.sjl.codegen.TypeInferenceProvider
import com.evg.sjl.exceptions.InvalidOperandTypesException
import com.evg.sjl.exceptions.VariableUsedWithoutBeingDeclaredException
import com.evg.sjl.lib.BinaryOperations.*
import com.evg.sjl.lib.UnaryOperations.NEGATION
import com.evg.sjl.parser.ast.*
import com.evg.sjl.parser.visitors.Visitor
import com.evg.sjl.values.NumberValue
import com.evg.sjl.values.StringValue
import com.evg.sjl.values.Types.NUMBER
import com.evg.sjl.values.Types.STRING
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*

class CodegenVisitor : Visitor {
    private val st = SymbolTable()
    private val il = InsnList()

    private val typeInference = TypeInferenceProvider(st)

    val instructions: InsnList
        get() = il

    override fun visit(statement: ExpressionStatement) {
        statement.expression.accept(this)
    }

    override fun visit(statement: PrintStatement) {
        il.add(FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"))
        statement.expression.accept(this)
        if (typeInference.getType(statement.expression) == STRING) {
            il.add(MethodInsnNode(INVOKEVIRTUAL,
                    "java/io/PrintStream",
                    if (statement.newLine)
                        "println"
                    else "print" ,
                    "(Ljava/lang/String;)V",
                    false))
            return
        }
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

    override fun visit(expression: ValueExpression) {
        when (expression.value) {
            is NumberValue ->
                il.add(LdcInsnNode(java.lang.Double(expression.value.value)))
            is StringValue ->
                il.add(LdcInsnNode(java.lang.String(expression.value.value)))
        }
    }

    override fun visit(expression: BinaryExpression) {
        val lt = typeInference.getType(expression.left)
        val rt = typeInference.getType(expression.right)
        when (lt) {
            NUMBER -> {
                expression.left.accept(this)
                expression.right.accept(this)
                if (rt != NUMBER)
                    throw InvalidOperandTypesException(expression.operation, rt, lt)
                il.add(InsnNode(when (expression.operation) {
                    ADDITION -> DADD
                    SUBTRACTION -> DSUB
                    MULTIPLICATION -> DMUL
                    DIVISION -> DDIV
                    REMAINDER -> DREM
                }))
            }
            STRING -> {
                if (rt == NUMBER) when (expression.operation) {
                    ADDITION -> {
                        il.add(TypeInsnNode(NEW, "java/lang/StringBuilder"))
                        il.add(InsnNode(DUP))
                        il.add(MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false))
                        expression.left.accept(this)
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
                        expression.right.accept(this)
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false))
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false))
                    }
                    else -> throw InvalidOperandTypesException(expression.operation, lt, rt)
                } else if (rt == STRING) when (expression.operation) {
                    ADDITION -> {
                        il.add(TypeInsnNode(NEW, "java/lang/StringBuilder"))
                        il.add(InsnNode(DUP))
                        il.add(MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false))
                        expression.left.accept(this)
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
                        expression.right.accept(this)
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false))
                        il.add(MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false))
                    }
                    else -> throw InvalidOperandTypesException(expression.operation, lt, rt)
                }
            }
        }

    }

    override fun visit(expression: UnaryExpression) {
        expression.expression.accept(this)
        val type = typeInference.getType(expression)
        if (type != NUMBER)
            throw InvalidOperandTypesException(expression.operation, type)
        if (expression.expression == NEGATION)
            il.add(InsnNode(DNEG))
    }

    override fun visit(statement: VariableDefinitionStatement) {
        val symbol = st.register(statement.identifier, statement.type)
        when (statement.type) {
            NUMBER -> {
                il.add(LdcInsnNode(java.lang.Double(0.0)))
                il.add(VarInsnNode(DSTORE, symbol.index))
            }
            STRING -> {
                il.add(LdcInsnNode(java.lang.String("")))
                il.add(VarInsnNode(ASTORE, symbol.index))
            }
        }
    }

    override fun visit(statement: AssignmentStatement) {
        statement.expression.accept(this)
        if (statement.identifier in st.symbols) {
            val symbol = st[statement.identifier]
            if (symbol != null) when (typeInference.getType(statement.expression)) {
                NUMBER -> il.add(VarInsnNode(DSTORE, symbol.index))
                STRING -> il.add(VarInsnNode(ASTORE, symbol.index))
            }
        } else {
            val symbol = st.register(statement.identifier, typeInference.getType(statement.expression))
            when (symbol.type) {
                NUMBER -> il.add(VarInsnNode(DSTORE, symbol.index))
                STRING -> il.add(VarInsnNode(ASTORE, symbol.index))
            }
        }
    }

    override fun visit(expression: VariableExpression) {
        val sym = st[expression.identifier]
                ?: throw VariableUsedWithoutBeingDeclaredException(expression.identifier)
        when (sym.type) {
            NUMBER -> il.add(VarInsnNode(DLOAD, sym.index))
            STRING -> il.add(VarInsnNode(ALOAD, sym.index))
        }
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