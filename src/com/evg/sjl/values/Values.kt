package com.evg.sjl.values

interface Value {
    val type: Primitives
}

class IntegerValue(val value: Int) : Value {
    override val type: Primitives
        get() = Primitives.INTEGER
}

class DoubleValue(val value: Double) : Value {
    override val type: Primitives
        get() = Primitives.DOUBLE
}

class StringValue(val value: String) : Value {
    override val type: Primitives
        get() = Primitives.STRING
}

class BooleanValue(val value: Boolean) : Value {
    override val type: Primitives
        get() = Primitives.BOOLEAN
}