package com.evg.sjl.exceptions

import com.evg.sjl.ast.Expression
import com.evg.sjl.lib.Operations
import com.evg.sjl.values.Type

abstract class CompileException(message: String)
    : SJLException(message)

class VariableUsedWithoutBeingDeclaredException(identifier: String)
    : CompileException("Variable $$identifier used without being declared")

class VariableAlreadyDeclaredException(identifier: String)
    : CompileException("Variable $$identifier has multiple declarations in the same scope")

class InvalidOperandTypesException(operation: Operations, vararg operandTypes: Type)
    : CompileException("Unable to perform operation $operation with following operand types: ${operandTypes.joinToString(", ")}")

class InvalidCastException(from: Type, to: Type)
    : CompileException("Invalid type cast $from -> $to")

class InvalidValueTypeException(type: Type)
    : CompileException("Invalid value type: $type")

class TypeInferenceFailException
    : CompileException("Unable to determine value type")

class MissingInitializerException(identifier: String)
    : CompileException("Variable $$identifier is missing initializer")

class InvalidAssignmentTargetException(expression: Expression)
    : CompileException("Invalid assignment target: ${expression.stringify()}")

class MissingTypeParametersException(name: String, count: Int)
    : CompileException("Type @$name is missing type parameters[$count]")