package com.evg.sjl.exceptions

import com.evg.sjl.lib.Operations
import com.evg.sjl.values.Types

abstract class CompileException(message: String)
    : SJLException(message)

class VariableUsedWithoutBeingDeclaredException(identifier: String)
    : CompileException("Variable $$identifier used without being declared")

class VariableAlreadyDeclaredException(identifier: String)
    : CompileException("Variable $$identifier has multiple declarations in the same scope")

class InvalidOperandTypesException(operation: Operations, vararg operandTypes: Types)
    : CompileException("Unable to perform operation $operation with following operand types: ${operandTypes.joinToString(", ")}")

class InvalidCastException(from: Types, to: Types)
    : CompileException("Invalid type cast $from -> $to")

class InvalidValueTypeException(type: Types)
    : CompileException("Invalid value type: $type")

class TypeInferenceFailException
    : CompileException("Unable to determine value type")