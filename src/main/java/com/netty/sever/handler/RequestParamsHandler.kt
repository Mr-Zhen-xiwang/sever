package com.netty.sever.handler

import com.netty.sever.entity.HttpRequest
import java.lang.reflect.Method

/**
 *@ClassName RequestParamsHandler
 *@Description 请求参数的解析和对应处理方法入参映射
 *@Author zhenxiwang
 *@Date 2020/5/21 10:41
 */
class RequestParamsHandler {

    companion object {
        /**
         * @Author zhen_xiwang
         * @Description 参数解析&&映射
         * @Date 11:10 2020/5/21
         */
        fun handleAndMappingParams(method: Method, httpRequest: HttpRequest): Array<Any?> {
            val list = mutableListOf<Any?>()
            method.parameters.forEach {
                val paramName = it.name
                httpRequest.body[paramName].let { inn ->
                    if (inn == null) {
                        list.add(null)
                    } else {
                        list.add(castClass(httpRequest.body[paramName]
                                ?: error("$paramName 参数不能为空"), it.type))
                    }

                }
            }
            return list.toTypedArray()
        }

    }
}

/**
 * @Author zhen_xiwang
 * @Description any转类型
 * @Date 13:58 2020/5/21
 */
private inline fun castClass(any: Any, clazz: Class<*>): Any {
    var any = any.toString()

    if (clazz == String::class.java) {
        return any
    }

    if (clazz == Int::class.javaPrimitiveType || clazz == Int::class.java) {
        return any.toInt()
    }

    if (clazz == Boolean::class.javaPrimitiveType) {
        return any.toBoolean()
    }

    if (clazz == Double::class.javaPrimitiveType) {
        return any.toDouble()
    }

    if (clazz == Long::class.javaPrimitiveType) {
        return any.toLong()
    }
    return any
}