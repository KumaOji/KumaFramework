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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.datascope.aspect;

import com.kuma.boot.common.holder.UserContextHolder;
import com.kuma.boot.common.model.BaseSecurityUser;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascope.datascope.annotation.DataScope;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/** 数据过滤处理（部门/自定义数据权限） */
@Aspect
@Component("dataScopeDeptAspect")
public class DataScopeAspect {
    /** 全部数据权限 */
    public static final String DATA_SCOPE_ALL = "1";

    /** 自定数据权限 */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /** 部门数据权限 */
    public static final String DATA_SCOPE_DEPT = "3";

    /** 部门及以下数据权限 */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /** 仅本人数据权限 */
    public static final String DATA_SCOPE_SELF = "5";

    /** 数据权限过滤关键字 */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        // 获取当前的用户
        BaseSecurityUser loginUser = UserContextHolder.getUser();
        if (StringUtils.isNotNull(loginUser)) {
            // 如果是超级管理员，则不过滤数据
            if (!loginUser.isAdmin()) {
                dataScopeFilter(
                        joinPoint,
                        loginUser,
                        controllerDataScope.deptAlias(),
                        controllerDataScope.userAlias());
            }
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     */
    public static void dataScopeFilter(
            JoinPoint joinPoint, BaseSecurityUser user, String deptAlias, String userAlias) {
        // StringBuilder sqlString = new StringBuilder();
        // List<String> conditions = new ArrayList<String>();
        // for (SysRole role : user.getRoles())
        // {
        //    String dataScope = role.getDataScope();
        //    if (!DATA_SCOPE_CUSTOM.equals(dataScope) && conditions.contains(dataScope))
        //    {
        //        continue;
        //    }
        //    if (DATA_SCOPE_ALL.equals(dataScope))
        //    {
        //        sqlString = new StringBuilder();
        //        break;
        //    }
        //    else if (DATA_SCOPE_CUSTOM.equals(dataScope))
        //    {
        //        sqlString.append(StringUtils.format(
        //                " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {}
        // ) ", deptAlias,
        //                role.getRoleId()));
        //    }
        //    else if (DATA_SCOPE_DEPT.equals(dataScope))
        //    {
        //        sqlString.append(StringUtils.format(" OR {}.dept_id = {} ", deptAlias,
        // user.getDeptId()));
        //    }
        //    else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope))
        //    {
        //        sqlString.append(StringUtils.format(
        //                " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or
        // find_in_set( {} , ancestors ) )",
        //                deptAlias, user.getDeptId(), user.getDeptId()));
        //    }
        //    else if (DATA_SCOPE_SELF.equals(dataScope))
        //    {
        //        if (StringUtils.isNotBlank(userAlias))
        //        {
        //            sqlString.append(StringUtils.format(" OR {}.user_id = {} ", userAlias,
        // user.getUserId()));
        //        }
        //        else
        //        {
        //            // 数据权限为仅本人且没有userAlias别名不查询任何数据
        //            sqlString.append(StringUtils.format(" OR {}.dept_id = 0 ", deptAlias));
        //        }
        //    }
        //    conditions.add(dataScope);
        // }
        //
        // if (StringUtils.isNotBlank(sqlString.toString()))
        // {
        //    Object params = joinPoint.getArgs()[0];
        //    if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        //    {
        //        BaseEntity baseEntity = (BaseEntity) params;
        //        baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
        //    }
        // }
    }

    /** 拼接权限sql前先清空params.dataScope参数防止注入 */
    private void clearDataScope(final JoinPoint joinPoint) {
        // Object params = joinPoint.getArgs()[0];
        // if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        // {
        //    BaseEntity baseEntity = (BaseEntity) params;
        //    baseEntity.getParams().put(DATA_SCOPE, "");
        // }
    }
}
