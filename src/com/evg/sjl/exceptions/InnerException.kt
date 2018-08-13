package com.evg.sjl.exceptions

class InnerException(override val cause: Throwable)
    : SJLException("Inner exception occurred: $cause")