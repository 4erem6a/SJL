package com.evg.sjl

import com.evg.sjl.codegen.BytecodeGenerator
import com.evg.sjl.codegen.BytecodeLoader
import com.evg.sjl.exceptions.InnerException
import com.evg.sjl.exceptions.LexicalException
import com.evg.sjl.exceptions.SJLException
import com.evg.sjl.exceptions.SyntaxException
import com.evg.sjl.lexer.Lexer
import com.evg.sjl.lexer.Token
import com.evg.sjl.parser.Parser
import com.evg.sjl.ast.Node
import com.evg.sjl.ast.visitors.PrintVisitor

class SJL private constructor(private val ast: Node) {
    companion object {
        @JvmStatic
        val VERSION: String = "1.5.0"

        @JvmStatic
        @Throws(LexicalException::class)
        fun tokenize(source: String) = Lexer(source).tokenize()

        @JvmStatic
        @Throws(SyntaxException::class)
        fun parse(tokens: List<Token>) = Parser(tokens).parse()

        @JvmStatic
        @Throws(LexicalException::class, SyntaxException::class)
        fun from(source: String) = SJL(parse(tokenize(source)))

        @JvmStatic
        @Throws(SyntaxException::class)
        fun from(tokens: List<Token>) = SJL(parse(tokens))

        fun from(ast: Node) = SJL(ast)
    }

    fun stringify(): String {
        val pv = PrintVisitor()
        ast.accept(pv)
        return pv.toString()
    }

    val bytecode
        get() = BytecodeGenerator(ast).generate()

    val clazz
        get() = BytecodeLoader.loadClass(bytecode)

    val runnable
        get() = clazz.newInstance() as Runnable
}

fun process(source: String) {
    try {
        SJL.from(source).runnable.run()
    } catch (e: SJLException) {
        throw e
    } catch (e: Exception) {
        throw InnerException(e)
    }
}