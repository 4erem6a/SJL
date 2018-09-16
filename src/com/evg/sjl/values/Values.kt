package com.evg.sjl.values

interface Value {
    val type: Types
}

class IntegerValue(val value: Int) : Value {
    override val type: Types
        get() = Types.INTEGER
}

class DoubleValue(val value: Double) : Value {
    override val type: Types
        get() = Types.DOUBLE
}

class StringValue(val value: String) : Value {
    override val type: Types
        get() = Types.STRING
}

enum class Types(val defaultValue: Any) {
    INTEGER(0),
    DOUBLE(0.0),
    STRING("")
}