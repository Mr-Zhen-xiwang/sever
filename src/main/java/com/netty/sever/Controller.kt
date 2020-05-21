package com.netty.sever

import com.netty.sever.annotation.request.Controller
import com.netty.sever.annotation.request.Get
import com.netty.sever.annotation.request.Post

/**
 *@ClassName Controller
 *@Description TODO
 *@Author zhenxiwang
 *@Date 2020/5/20 17:48
 */
@Controller("/demo")
class Controller {
    @Get("/get")
    fun get(name: String): Any {
        println(name)
        return "hello world:get!!"
    }

    @Post("/post")
    fun post(name: String): Any {
        println(name)
        return "hello world:post!!"
    }
}

data class Person(
        var name: String,
        var id: Int
)

