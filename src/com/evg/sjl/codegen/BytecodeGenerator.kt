package com.evg.sjl.codegen

import com.evg.sjl.parser.ast.Node
import jdk.internal.org.objectweb.asm.ClassWriter
import jdk.internal.org.objectweb.asm.Opcodes.*
import jdk.internal.org.objectweb.asm.tree.*

class BytecodeGenerator(private val ast: Node) {
    companion object {
        const val generatedClassName = "com/evg/SJLRunnable"
        const val scannerFieldName = "scanner"
    }

    fun generate(): ByteArray {
        val cn = ClassNode()

        cn.version = V1_8
        cn.access = ACC_PUBLIC + ACC_SUPER
        cn.name = generatedClassName
        cn.superName = "java/lang/Object"
        cn.interfaces.add("java/lang/Runnable")

        run {
            val fn = FieldNode(ACC_PUBLIC + ACC_FINAL, scannerFieldName, "Ljava/util/Scanner;", null, null)
            cn.fields.add(fn)
        }

        run {
            val mn = MethodNode(ACC_PUBLIC, "<init>", "()V", null, null)
            val il = mn.instructions

            il.add(VarInsnNode(ALOAD, 0))
            il.add(MethodInsnNode(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false))

            il.add(VarInsnNode(ALOAD, 0))
            il.add(TypeInsnNode(NEW, "java/util/Scanner"))
            il.add(InsnNode(DUP))
            il.add(FieldInsnNode(GETSTATIC, "java/lang/System", "in", "Ljava/io/InputStream;"))
            il.add(MethodInsnNode(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V", false))
            il.add(FieldInsnNode(GETSTATIC, "java/util/Locale", "ENGLISH", "Ljava/util/Locale;"))
            il.add(MethodInsnNode(INVOKEVIRTUAL, "java/util/Scanner", "useLocale", "(Ljava/util/Locale;)Ljava/util/Scanner;", false))
            il.add(FieldInsnNode(PUTFIELD, generatedClassName, scannerFieldName, "Ljava/util/Scanner;"))
            il.add(InsnNode(RETURN))

            cn.methods.add(mn)
        }

        run {
            val mn = MethodNode(ACC_PUBLIC, "run", "()V", null, null)

            mn.instructions = run {
                val symbolTable = SymbolTable()
                val compilationContext = CompilationContext(symbolTable, InsnList(), TypeInferenceProvider(symbolTable))

                ast.compile(compilationContext)

                compilationContext.il
            }

            mn.instructions.add(InsnNode(RETURN))

            cn.methods.add(mn)
        }

        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)

        cn.accept(cw)

        return cw.toByteArray()
    }
}