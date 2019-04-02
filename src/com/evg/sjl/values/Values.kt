package com.evg.sjl.values

interface Value {
    val type: Type
}

class IntegerValue(val value: Int) : Value {
    override val type: Type
        get() = Primitives.INTEGER
}

class DoubleValue(val value: Double) : Value {
    override val type: Type
        get() = Primitives.DOUBLE
}

class StringValue(val value: String) : Value {
    override val type: Type
        get() = StringType
}

class BooleanValue(val value: Boolean) : Value {
    override val type: Type
        get() = Primitives.BOOLEAN
}