package com.evg.sjl.values

interface Type

enum class Primitives(val defaultValue: Value): Type {
    INTEGER(IntegerValue(0)),
    DOUBLE(DoubleValue(0.0)),
    STRING(StringValue("")),
    BOOLEAN(BooleanValue(false))
}

class ArrayType(val type: Type): Type {
    override fun toString(): String = "$type[]"
}