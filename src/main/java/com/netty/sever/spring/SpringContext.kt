package com.netty.sever.spring

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.stereotype.Component

/**
 *@ClassName SpringContext
 *@Description ioc容器
 *@Author zhenxiwang
 *@Date 2020/5/20 16:48
 */
@Component
object SpringContext : ApplicationContextAware {

    @Volatile
    private var context: ApplicationContext? = null

    override fun setApplicationContext(context: ApplicationContext) {
        SpringContext.context = context
    }

    fun getContext(): ApplicationContext? {
        return context
    }

    /**
     * @Author zhen_xiwang
     * @Description 初始化ioc容器
     * @Date 18:47 2020/5/20
     */
    fun initContext(clazz: Class<*>): ApplicationContext {
        synchronized(this.javaClass) {
            context?.also { println("ApplicationContext is running") }
                    ?: run {
                        if (AnnotationUtils.findAnnotation(clazz, ComponentScan::class.java) == null) {
                            //使用ComponentScan 扫描bean对象并注入
                            throw IllegalStateException("startup class [" + clazz.name + "] must be Annotation ComponentScan and choose scan package")
                        }
                        val annoContext = AnnotationConfigApplicationContext()
                        annoContext.register(clazz)
                        annoContext.refresh()
                        context = annoContext
                    }
            println("context-init:$context")
        }
        return context!!
    }
}