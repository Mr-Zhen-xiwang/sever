package com.netty.sever.entity

import com.esotericsoftware.reflectasm.MethodAccess

/**
 *@ClassName HttpHandlerMethod
 *@Description 处理请求的handlerMethod
 *@Author zhenxiwang
 *@Date 2020/5/20 15:49
 */
class HttpHandlerMethod(
        /**
         * class字节码对象
         */
        var clazz: Class<*>,

        /**
         * index 执行方法的index
         */
        var index: Int,

        /**
         * 反射实例
         */
        var method: MethodAccess,

        /**
         * 参数类型
         */
        var paramType: Array<Class<*>>,

        /**
         * 处理type: 暂时只有GET,POST
         */
        var type: String
)

