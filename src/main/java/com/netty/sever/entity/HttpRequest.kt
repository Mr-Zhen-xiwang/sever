package com.netty.sever.entity

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.QueryStringDecoder
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.MemoryAttribute
import io.netty.util.CharsetUtil
import java.net.URI
import java.util.HashMap

/**
 *@ClassName HttpRequest
 *@Description 请求
 *@Author zhenxiwang
 *@Date 2020/5/21 10:48
 */
class HttpRequest(
        /**
         * 请求uri
         */
        var uri: String = "",
        /**
         * 请求头
         */
        var headers: HttpHeaders,
        /**
         * 请求体（头信息中参数也会封装到body中）
         */
        var body: Map<String, Any>
) {
    companion object {

        fun analysisFullHttpRequest(fullHttpRequest: FullHttpRequest): HttpRequest {
            return HttpRequest(fullHttpRequest.uri, fullHttpRequest.headers(), fullHttpRequest.analysisParam())
        }


        /**
         * @Author zhen_xiwang
         * @Description 解析body体中的参数
         * @Date 11:29 2020/5/21
         */

    }
}

/**
 * @Author zhen_xiwang
 * @Description 解析uri&&body中的参数
 * @Date 11:29 2020/5/21
 */
inline fun FullHttpRequest.analysisParam(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    //解析uri中参数
    val decoder = QueryStringDecoder(uri())
    for (entry in decoder.parameters().entries) {
        map[entry.key] = entry.value[0]
    }
    if (this.method() != HttpMethod.POST) {
        return map
    }
    var strContentType = headers().get("Content-Type")
    strContentType = strContentType?.trim() ?: ""
    if (strContentType.contains("application/json")) {
        //json的body体
        val jsonBuf = this.content()
        val jsonStr = jsonBuf.toString(CharsetUtil.UTF_8)
        if (!jsonStr.isNullOrBlank()) {
            map.putAll(ObjectMapper().readValue(jsonStr, Map::class.java) as Map<String, Any>)
        }
    } else {
        //表单body体
        val httpPostRequestDecoder = HttpPostRequestDecoder(DefaultHttpDataFactory(false), this)
        val postData = httpPostRequestDecoder!!.bodyHttpDatas
        for (data in postData) {
            if (data.httpDataType == InterfaceHttpData.HttpDataType.Attribute) {
                val attribute = data as MemoryAttribute
                map[attribute.name] = attribute.value
            }
        }
    }

    return map
}