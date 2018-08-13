package com.evg.sjl

import com.evg.sjl.codegen.ByteCodeGenerator
import com.evg.sjl.codegen.byteCodeLoader
import com.evg.sjl.exceptions.InnerException
import com.evg.sjl.exceptions.SJLException
import com.evg.sjl.lexer.Lexer
import com.evg.sjl.lexer.Token
import com.evg.sjl.parser.Parser
import com.evg.sjl.parser.ast.Node
import com.evg.sjl.parser.visitors.PrintVisitor

class SJL(private val source: String) {
    @Throws(SJLException::class)
    fun tokenize(): List<Token> = Lexer(source).tokenize()

    @Throws(SJLException::class)
    fun parse(): Node = Parser(tokenize()).parse()

    @Throws(SJLException::class)
    fun compile(): Runnable {
        try {
            return byteCodeLoader.loadClass(ByteCodeGenerator(parse()).generate()).newInstance() as Runnable
        } catch (e: SJLException) {
            throw e
        } catch (e: Exception) {
            throw InnerException(e)
        }
    }

    @Throws(SJLException::class)
    fun listing(): String {
        val pv = PrintVisitor()
        parse().accept(pv)
        return pv.toString()
    }
}