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

    if (args.version) {
        println("SJL version: ${SJL.VERSION}")
        return
    }

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
        println("Either a source file or a source code must be specified")
        return
    }

    try {
        val sjl = SJL(source)

        if (args.execute) {
            val runnable = sjl.compile()
            val execTime = measureTimeMillis { runnable.run() }
            if (args.measureExecTime)
                println("\nExecution took ${execTime}ms")
        }

        if (args.printTokens)
            println("Token list:\n${sjl.tokenize().joinToString("\n")}end.")

        if (args.printListing)
            println("Program listing: ${sjl.listing()}")

    } catch (e: LexicalException) {
        System.err.println("Lexical error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: SyntaxException) {
        System.err.println("Syntax error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: CompileException) {
        System.err.println("Compilation error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
    } catch (e: InnerException) {
        System.err.println("Inner error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
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
    @Parameter(names = ["-l", "--listing"], description = "Print program listing", order = 5)
    var printListing = false
    @Parameter(names = ["-v", "--version"], description = "Display compiler version", order = 6)
    var version = false
    @Parameter(names = ["-h", "--help"], description = "Display this message", help = true, order = 7)
    var help = false
}