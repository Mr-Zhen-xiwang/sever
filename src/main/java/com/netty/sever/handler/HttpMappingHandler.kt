package com.netty.sever.handler

import com.netty.sever.entity.HttpHandlerMethod

/**
 *@ClassName HttpMappingHandler
 *@Description 请求映射处理器
 *@Author zhenxiwang
 *@Date 2020/5/20 15:39
 */
class HttpMappingHandler {
    companion object {
        private val httpHandler = mutableMapOf<String, HttpHandlerMethod>()

        /**
         * @Author zhen_xiwang
         * @Description 匹配请求处理器
         * @Date 16:33 2020/5/20
         */
        fun getHttpHandler(uri: String): HttpHandlerMethod? {
            return httpHandler[uri]
        }

        /**
         * @Author zhen_xiwang
         * @Description  注册请求处理器
         * @Date 15:47 2020/5/20
         */
        fun register(uri: String, method: HttpHandlerMethod) {
            httpHandler[uri] = method
        }
    }


}