package com.netty.sever

import com.netty.sever.httpServer.HttpServer
import org.springframework.context.annotation.ComponentScan

/**
 *@ClassName Run
 *@Description 启动入口
 *@Author zhenxiwang
 *@Date 2020/5/20 17:57
 */
@ComponentScan(value = ["com.netty.sever"])
class Run

fun main() {
    HttpServer(6677).run(Run::class.java)
}