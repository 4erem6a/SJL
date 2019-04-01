package com.evg.sjl.values

import jdk.internal.org.objectweb.asm.Opcodes

interface Type {
    val jvmType: String
}

enum class Primitives(val defaultValue: Value) : Type {
    INTEGER(IntegerValue(0)) {
        override val jvmType = "I"
        override val jvmCode = Opcodes.T_INT
    },
    DOUBLE(DoubleValue(0.0)) {
        override val jvmType = "D"
        override val jvmCode = Opcodes.T_DOUBLE
    },
    BOOLEAN(BooleanValue(false)) {
        override val jvmType = "Z"
        override val jvmCode = Opcodes.T_BOOLEAN
    };

    abstract val jvmCode: Int
}

interface Referential

open class JavaClass(val name: String) : Type, Referential {
    override val jvmType: String
        get() = "L$name;"

    override fun equals(other: Any?) = other is JavaClass && other.name == this.name
    override fun hashCode(): Int {
        return name.hashCode()
    }
}

class StringType : JavaClass("java/lang/String") {
    companion object {
        @JvmStatic
        val defaultValue = StringValue("")
    }
}

class ArrayType(val type: Type) : Type, Referential {
    override val jvmType = "[${type.jvmType}"

    override fun toString(): String = "$type[]"

    override fun equals(other: Any?) = other is ArrayType && other.type == this.type
    override fun hashCode(): Int {
        return type.hashCode()
    }
}

class VoidType : Type {
    override val jvmType = "V"
}