package com.evg.sjl.exceptions

abstract class CompileException(message: String)
    : SJLException(message)

class VariableUsedWithoutBeingDeclaredException(identifier: String)
    : CompileException("Variable $$identifier used without being declared")