package com.netty.sever.httpServer

import com.netty.sever.error.SeverException
import com.netty.sever.handler.HttpMappingHandler
import com.netty.sever.handler.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*


/**
 * http处理
 */
class HttpHandler : SimpleChannelInboundHandler<FullHttpRequest>() {
    companion object {
        private val contentType = HttpHeaderValues.TEXT_PLAIN

        /**
         * 支持的请求方式
         * tip 暂时只支持get && post 请求
         */
        private val supportRequestMethod = listOf(HttpMethod.GET, HttpMethod.POST)
    }

    /**
     * 数据读取
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
        var response: DefaultFullHttpResponse? = null
        //不持支的请求方式
        try {
            response = if (!supportRequestMethod.contains(msg.method)) {
                //设置响应的消息体
                DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.BAD_REQUEST,
                        Unpooled.wrappedBuffer("not support  request method".toByteArray())) // 2
            } else if (HttpMappingHandler.getHttpHandler(getUri(msg.uri)) == null) {
                DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NOT_FOUND)
            } else {
                //处理请求
                val data = RequestHandler.requestResult(msg)
                DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(ObjectMapper().writeValueAsString(data).toByteArray())) // 2
            }
        } catch (e: SeverException) {
            //自定义异常处理
            e.printStackTrace()
            response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(ObjectMapper().writeValueAsString(e.message).toByteArray()))
        } catch (e: Exception) {
            e.printStackTrace()
            response = DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    Unpooled.wrappedBuffer("服务器发生错误".toByteArray()))
        }
        val heads: HttpHeaders = response!!.headers()
        heads.add(HttpHeaderNames.CONTENT_TYPE, "$contentType; charset=UTF-8")
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()) // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)

        ctx.write(response)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        println("channelReadComplete")
        super.channelReadComplete(ctx)
        ctx.flush(); // 4
    }

    /**
     * 异常捕获
     */
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        println("exceptionCaught")
        cause?.printStackTrace()
        ctx?.close()
    }

}

inline fun getUri(uri: String): String {
    return if (uri.contains("?")) uri.split("?")[0]
    else uri

}