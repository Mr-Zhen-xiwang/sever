package com.netty.sever.annotation.request

import org.springframework.stereotype.Component

@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
annotation class Post(
        val uri: String = ""
) {
}