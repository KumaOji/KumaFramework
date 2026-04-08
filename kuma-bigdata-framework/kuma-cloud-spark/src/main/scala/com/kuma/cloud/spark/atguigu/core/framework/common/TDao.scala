package com.kuma.cloud.spark.atguigu.core.framework.common

import com.kuma.cloud.spark.atguigu.core.framework.util.EnvUtil


trait TDao {

    def readFile(path:String) = {
        EnvUtil.take().textFile(path)
    }
}
