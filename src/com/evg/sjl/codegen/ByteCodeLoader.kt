package com.evg.sjl.codegen

object ByteCodeLoader : ClassLoader() {
    fun loadClass(bytes: ByteArray): Class<*> = defineClass(null, bytes, 0, bytes.size)
}