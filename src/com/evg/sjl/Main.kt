package com.evg.sjl

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.evg.sjl.exceptions.CompileException
import com.evg.sjl.exceptions.InnerException
import com.evg.sjl.exceptions.LexicalException
import com.evg.sjl.exceptions.SyntaxException
import java.io.File
import kotlin.system.measureTimeMillis

fun main(argv: Array<String>) {
    val args = Args()
    val jcommander = JCommander.newBuilder()
            .addObject(args)
            .build()

    jcommander.programName = "sjl"

    try {
        jcommander.parse(*argv)
    } catch (e: Exception) { }

    if (args.help)
        jcommander.usage()

    val source = args.sourceCode ?: if (args.sourceFile != null) {
        val file = File(args.sourceFile)
        if (file.exists() && file.canRead())
            file.readText()
        else {
            println("File does not exists or is not readable")
            return
        }
    } else {
        println("Either source file or source code must be specified")
        return
    }

    try {
        val sjl = SJL(source)

        if (args.execute) {
            val execTime = measureTimeMillis { sjl.compile().run() }
            if (args.measureExecTime)
                println("\nExecution took ${execTime}ms")
        }

        if (args.printTokens)
            println("Token list:\n${sjl.tokenize().map { it.toString() + '\n' }}end.")

        if (args.printAST)
            println("Abstract Syntax tree:\n${sjl.parse()}\nend.")

        if (args.printListing)
            println("Program listing:\n${sjl.listing()}end.")

    } catch (e: LexicalException) {
        error("Lexical error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: SyntaxException) {
        error("Syntax error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: CompileException) {
        error("Compilation error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: InnerException) {
        error("Inner error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    }
}

private class Args {
    @Parameter(names = ["-s", "--source"], description = "The source code", order = 0)
    var sourceCode: String? = null
    @Parameter(names = ["-f", "--file"], description = "The source file", order = 1)
    var sourceFile: String? = null
    @Parameter(names = ["-e", "--execute"], description = "Execute source code", order = 2)
    var execute = false
    @Parameter(names = ["-m", "--measure"], description = "Toggle execution time measurement", order = 3)
    var measureExecTime = false
    @Parameter(names = ["-t", "--tokens"], description = "Print token list", order = 4)
    var printTokens = false
    @Parameter(names = ["-ast"], description = "Print abstract syntax tree", order = 5)
    var printAST = false
    @Parameter(names = ["-l", "--listing"], description = "Print program listing", order = 6)
    var printListing = false
    @Parameter(names = ["-h", "--help"], description = "Display this message", help = true, order = 7)
    var help = false
}