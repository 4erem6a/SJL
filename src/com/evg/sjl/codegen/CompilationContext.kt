package com.evg.sjl.codegen

import jdk.internal.org.objectweb.asm.tree.InsnList

class CompilationContext(
        val symbolTable: SymbolTable,
        val il: InsnList,
        val typeInference: TypeInferenceProvider
)