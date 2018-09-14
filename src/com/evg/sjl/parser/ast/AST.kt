package com.evg.sjl.parser.ast

import com.evg.sjl.codegen.CompilationContext
import com.evg.sjl.parser.visitors.Visitor

interface Node {
    fun accept(visitor: Visitor)
    fun compile(context: CompilationContext)
}

interface Statement : Node

interface Expression : Node