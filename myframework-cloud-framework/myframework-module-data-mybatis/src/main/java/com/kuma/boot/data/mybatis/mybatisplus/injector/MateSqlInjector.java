/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.mybatisplus.injector;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.InsertBatch;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.InsertIgnore;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.InsertIgnoreBatch;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.Replace;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.ReplaceBatch;
import com.kuma.boot.data.mybatis.mybatisplus.injector.methods.UpdateAllById;
import java.util.List;
import org.apache.ibatis.session.Configuration;
import cn.hutool.core.util.ArrayUtil;

/**
 * 自定义的 sql 注入
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-04 07:44:06
 */
public class MateSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        // 增加 SelectByErp对象，程序启动后自动加载
        // methodList.add(new SelectByErp(SelectByErp.method));
        // 添加 AlwaysUpdateSomeColumnById 对象 该方法可以在更新时只更新我需要的字段，不进行全字段更新。添加步骤如下。

        methodList.add(new InsertBatch());
        methodList.add(new InsertIgnore());
        methodList.add(new InsertIgnoreBatch());
        methodList.add(new Replace());
        methodList.add(new ReplaceBatch());

        // 增加自定义方法
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        //methodList.add(new AlwaysUpdateSomeColumnById());
        methodList.add(
                new UpdateAllById(
                        field ->
                                !ArrayUtil.containsAny(
                                        new String[] {"create_time", "created_by"},
                                        field.getColumn())));

        // methodList.add(new UpdateBatchMethod());
        return methodList;
    }
}
