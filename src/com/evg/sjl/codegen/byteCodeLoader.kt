package com.evg.sjl.codegen

object byteCodeLoader : ClassLoader() {
    fun loadClass(bytes: ByteArray) = defineClass(null, bytes, 0, bytes.size)
}