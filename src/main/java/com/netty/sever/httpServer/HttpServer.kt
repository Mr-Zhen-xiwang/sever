package com.netty.sever.httpServer

import com.esotericsoftware.reflectasm.MethodAccess
import com.netty.sever.annotation.request.Controller
import com.netty.sever.annotation.request.Get
import com.netty.sever.annotation.request.Post
import com.netty.sever.entity.HttpHandlerMethod
import com.netty.sever.handler.HttpMappingHandler
import com.netty.sever.spring.SpringContext
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import kotlin.Int

/**
 * @Author zhen_xiwang
 * @Description  http服务器启动类
 * @Date 10:44 2020/5/19
 */
class HttpServer(
        private var port: Int
) {
    /**
     * 启动方法
     */
    private fun start() {
        //启动你nio服务的辅助启动类，配置netty的一些参数：如接受的数据缓存大小
        //可以直接使用channel，一般不会直接这么做
        val bootstrap = ServerBootstrap()

        //线程组1：接受client请求
        val bossGroup = NioEventLoopGroup()
        //线程组2：业务处理操作
        val workerGroup = NioEventLoopGroup()

        //创建业务需求的channelinitializer，进行初始化
        bootstrap.group(bossGroup, workerGroup)
                //指定nioServerSocketChannel类型的channel
                .channel(NioServerSocketChannel::class.java)
                //自定义childHandler，处理业务
                .childHandler(object : ChannelInitializer<SocketChannel>() {
                    //重写channel初始化的方法
                    override fun initChannel(channel: SocketChannel) {
                        println("<<<<<<<<<<<<<<<<<<<初始化>>>>>>>>>>>>>>>>>>>>>>>>>>>")
                        channel.pipeline()
                                //用于解码request
                                .addLast("decoder", HttpRequestDecoder())
                                //用于编码response
                                .addLast("encoder", HttpResponseEncoder())
                                //消息聚合器（重要），设置消息合并的数据大小
                                .addLast("aggregator", HttpObjectAggregator(512 * 1024))
                                //自定义的childHandler,处理消息和请求
                                .addLast("handler", HttpHandler())
                    }
                })
                //对channel使用或者移除ChannelOption
                .option(ChannelOption.SO_BACKLOG, 128)
                //对childChannel使用或者移除ChannelOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)
        bootstrap.bind(this.port).sync()
    }

    fun run(clazz: Class<*>) {
        /**
         * 1.注册context
         */
        val context = SpringContext.initContext(clazz)

        /**
         * 注册处理http的handler（即对应方法）
         */
        context.getBeansWithAnnotation(Controller::class.java).forEach {
            val clazz = it.value::class.java
            val methodAccess = MethodAccess.get(clazz)
            clazz.methods.forEach { inn ->
                if (inn.isAnnotationPresent(Get::class.java)) {
                    val uri = clazz.getAnnotation(Controller::class.java).uri + inn.getAnnotation(Get::class.java).uri
                    HttpMappingHandler.register(uri, HttpHandlerMethod(clazz, methodAccess.getIndex(inn.name, *inn.parameterTypes), methodAccess, inn.parameterTypes, "GET"))
                }
                if (inn.isAnnotationPresent(Post::class.java)) {
                    val uri = clazz.getAnnotation(Controller::class.java).uri + inn.getAnnotation(Post::class.java).uri
                    HttpMappingHandler.register(uri, HttpHandlerMethod(clazz, methodAccess.getIndex(inn.name, *inn.parameterTypes), methodAccess, inn.parameterTypes, "POST"))
                }
            }
        }

        start()
        println("<<<<<<<<<<<<<<<<<<<     sever start       >>>>>>>>>>>>>>>>>>>")
        println("<<<<<<<<<<<<<<<<<<< listen port ：${port} >>>>>>>>>>>>>>>>>>>")
    }
}
