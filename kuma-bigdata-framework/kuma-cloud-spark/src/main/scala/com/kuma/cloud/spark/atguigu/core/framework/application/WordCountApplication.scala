package com.kuma.cloud.spark.atguigu.core.framework.application

import com.kuma.cloud.spark.atguigu.core.framework.common.TApplication
import com.kuma.cloud.spark.atguigu.core.framework.controller.WordCountController


object WordCountApplication extends App with TApplication{

    // 启动应用程序
    start(){
        val controller = new WordCountController()
        controller.dispatch()
    }

}
