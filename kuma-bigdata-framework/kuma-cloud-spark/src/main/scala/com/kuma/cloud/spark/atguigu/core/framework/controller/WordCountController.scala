package com.kuma.cloud.spark.atguigu.core.framework.controller

import com.kuma.cloud.spark.atguigu.core.framework.common.TController
import com.kuma.cloud.spark.atguigu.core.framework.service.WordCountService


/**
  * 控制层
  */
class WordCountController extends TController {

    private val wordCountService = new WordCountService()

    // 调度
    def dispatch(): Unit = {
        // TODO 执行业务操作
        val array = wordCountService.dataAnalysis()
        array.foreach(println)
    }
}
