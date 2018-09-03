package com.evg.sjl.values

interface Value {
    val type: Types
}

class NumberValue(val value: Double) : Value {
    override val type: Types
        get() = Types.NUMBER
}

class StringValue(val value: String) : Value {
    override val type: Types
        get() = Types.STRING
}

enum class Types {
    NUMBER,
    STRING
}