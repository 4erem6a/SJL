package com.evg.sjl.lib

interface Operations

enum class BinaryOperations : Operations {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION,
    REMAINDER,
    RIGHT_SHIFT,
    LEFT_SHIFT,
    UNSIGNED_RIGHT_SHIFT,
    BITWISE_AND,
    BITWISE_XOR,
    BITWISE_OR
}

enum class UnaryOperations : Operations {
    NEGATION,
    BITWISE_NEGATION
}