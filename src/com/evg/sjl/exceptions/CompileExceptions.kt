package com.evg.sjl.exceptions

import com.evg.sjl.lib.Operations
import com.evg.sjl.values.Types

abstract class CompileException(message: String)
    : SJLException(message)

class VariableUsedWithoutBeingDeclaredException(identifier: String)
    : CompileException("Variable $$identifier used without being declared")

class InvalidOperandTypesException(operation: Operations, vararg operandTypes: Types)
    : CompileException("Unable to perform operation $operation with following operand types: ${operandTypes.joinToString(", ")}")