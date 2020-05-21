package com.netty.sever.handler

import com.netty.sever.entity.HttpRequest
import com.netty.sever.httpServer.getUri
import com.netty.sever.spring.SpringContext
import io.netty.handler.codec.http.FullHttpRequest
import org.springframework.util.ClassUtils

/**
 *@ClassName HttpHandler
 *@Description 请求处理器
 *@Author zhenxiwang
 *@Date 2020/5/19 14:50
 */
class RequestHandler {
    companion object {
        /**
         * @Author zhen_xiwang
         * @Description get&&post 请求处理器
         * @Date 14:50 2020/5/19
         */
        fun requestResult(msg: FullHttpRequest): Any {
            HttpMappingHandler.getHttpHandler(getUri(msg.uri)).let {
                //映射的字段名称和类型
                val params = RequestParamsHandler.handleAndMappingParams(ClassUtils.getMethod(it!!.clazz, it.method.methodNames[it.index], *it.paramType), HttpRequest.analysisFullHttpRequest(msg))
                return it.method.invoke(SpringContext.getContext()!!.getBean(it.clazz), it.index, *params)
            }
        }

    }
}

