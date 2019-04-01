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
    BITWISE_OR,
    BOOLEAN_AND,
    BOOLEAN_OR,
    BOOLEAN_XOR,
    EQUALS,
    NOT_EQUALS,
    LOWER_THAN,
    GREATER_THAN,
    EQUALS_OR_LOWER_THAN,
    EQUALS_OR_GREATER_THAN,
}

enum class UnaryOperations : Operations {
    NEGATION,
    BITWISE_NEGATION,
    BOOLEAN_NEGATION,
    ARRAY_LENGTH
}