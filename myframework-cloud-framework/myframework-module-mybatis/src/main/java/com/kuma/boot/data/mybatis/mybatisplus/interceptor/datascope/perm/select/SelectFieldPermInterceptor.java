/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.select;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.Permission;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.configuration.DataPermProperties;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.perm.local.DataPermContextHolder;
import java.util.Objects;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

/**
 * 查询字段权限拦截器
 */
@Component
public class SelectFieldPermInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private final DataPermProperties dataPermProperties;

    public SelectFieldPermInterceptor(DataPermProperties dataPermProperties) {
        this.dataPermProperties = dataPermProperties;
    }

    @Override
    public void beforeQuery(
            Executor executor,
            MappedStatement ms,
            Object parameter,
            RowBounds rowBounds,
            ResultHandler resultHandler,
            BoundSql boundSql) {
        // 配置是否开启了权限控制
        if (!dataPermProperties.isEnableSelectFieldPerm()) {
            return;
        }
        // 是否添加了对应的注解来开启数据权限控制
        Permission permission = DataPermContextHolder.getPermission();
        if (Objects.isNull(permission) || !permission.selectField()) {
            return;
        }
        // 检查是否已经登录和是否是超级管理员
        boolean admin =
                DataPermContextHolder.getUserDetail()
                        .map(BaseSecurityUser::isAdmin)
                        .orElseThrow(
                                () -> {
                                    throw new BusinessException("用户未登录");
                                });
        // 是否超级管理员
        if (admin) {
            return;
        }

        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        // 解析器
        mpBs.sql(parserSingle(mpBs.sql(), ms.getId()));
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        // SelectBody selectBody = select.getSelectBody();
        // if (selectBody instanceof PlainSelect plainSelect) {
        //     List<SelectItem> selectItems = plainSelect.getSelectItems();
        //
        //     plainSelect.setSelectItems(selectItems);
        // }
    }
}
