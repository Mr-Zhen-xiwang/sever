package com.netty.sever.annotation.request

import org.springframework.stereotype.Component

@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Component
annotation class Controller(
        val uri: String = ""
) {
}