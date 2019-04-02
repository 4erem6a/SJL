package com.evg.sjl

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.evg.sjl.exceptions.*
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
        if (args.execute || args.printListing) {
            val sjl = SJL.from(source)

            if (args.execute) {
                val runnable = sjl.runnable
                val execTime = measureTimeMillis { runnable.run() }
                if (args.measureExecTime)
                    println("\nExecution took ${execTime}ms")
            }

            if (args.printListing)
                println("Program listing: ${sjl.stringify()}")
        }

        if (args.printTokens)
            println("Token list:\n${SJL.tokenize(source).joinToString("\n")}\nend.")

    } catch (e: LexicalException) {
        System.err.println("Lexical error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
        args.debug(e)
    } catch (e: SyntaxException) {
        System.err.println("Syntax error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
        args.debug(e)
    } catch (e: CompileException) {
        System.err.println("Compilation error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
        args.debug(e)
    } catch (e: InnerException) {
        System.err.println("Inner error:\n\t${e.javaClass.simpleName}\n\t${e.message}")
        args.debug(e)
    }
}

private fun Args.debug(e: SJLException) {
    if (!this.debug)
        return
    e.printStackTrace()
}

private class Args {
    @Parameter(names = ["-s", "--source"], description = "The source code", order = 0)
    var sourceCode: String? = null
    @Parameter(names = ["-f", "--file"], description = "The source file", order = 1)
    var sourceFile: String? = null
    @Parameter(names = ["-o", "--output"], description = "Optional output file [not implemented]", order = 2)
    var outputFile: String? = null
    @Parameter(names = ["-e", "--execute"], description = "Execute source code", order = 3)
    var execute = false
    @Parameter(names = ["-m", "--measure"], description = "Toggle execution time measurement", order = 4)
    var measureExecTime = false
    @Parameter(names = ["-t", "--tokens"], description = "Print token list", order = 5)
    var printTokens = false
    @Parameter(names = ["-l", "--listing"], description = "Print program listing", order = 6)
    var printListing = false
    @Parameter(names = ["-d", "--debug"], description = "Enable debug mode", order = 7)
    var debug = false
    @Parameter(names = ["-v", "--version"], description = "Display compiler version", order = 8)
    var version = false
    @Parameter(names = ["-h", "--help"], description = "Display this message", help = true, order = 9)
    var help = false
}