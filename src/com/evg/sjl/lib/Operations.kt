package com.evg.sjl.lib

interface Operations

enum class BinaryOperations : Operations {
    ADDITION,
    SUBTRACTION,
    MULTIPLICATION,
    DIVISION
}

enum class UnaryOperations : Operations {
    NEGATION
}