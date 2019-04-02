package com.evg.sjl.lexer

import com.evg.sjl.exceptions.MissingTypeParametersException
import com.evg.sjl.values.*

object Typenames {
    val map = mapOf(
            "integer" to IntegerTypename,
            "int" to IntegerTypename,
            "boolean" to BooleanTypename,
            "bool" to BooleanTypename,
            "double" to DoubleTypename,
            "real" to DoubleTypename,
            "string" to StringTypename,
            "jvm" to JvmTypename,
            "void" to VoidTypename,
            "interface" to InterfaceTypename,
            "ji" to InterfaceTypename
    )
}

interface Typename {
    fun getType(args: List<String>): Type
}

object IntegerTypename : Typename {
    override fun getType(args: List<String>) = Primitives.INTEGER
}

object BooleanTypename : Typename {
    override fun getType(args: List<String>) = Primitives.BOOLEAN
}

object DoubleTypename : Typename {
    override fun getType(args: List<String>) = Primitives.DOUBLE
}

object StringTypename : Typename {
    override fun getType(args: List<String>) = StringType
}

object JvmTypename : Typename {
    override fun getType(args: List<String>) = JavaClass(args.firstOrNull() ?: "java/lang/Object")
}

object InterfaceTypename : Typename {
    override fun getType(args: List<String>): Type {
        val name = args.firstOrNull()
                ?: throw MissingTypeParametersException("interface", 1)
        return JavaInterface(name)
    }
}

object VoidTypename : Typename {
    override fun getType(args: List<String>) = VoidType
}