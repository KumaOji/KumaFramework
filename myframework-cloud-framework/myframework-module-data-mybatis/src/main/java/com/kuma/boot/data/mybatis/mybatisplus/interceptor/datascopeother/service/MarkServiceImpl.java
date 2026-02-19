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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.enums.ProvideTypeEnum;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.DataScopeInfo;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.Mark;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.MarkDto;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.MarkQuery;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.PageData;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.RoleMark;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.Rule;
import com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.RuleDto;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * MarkServiceImpl
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
@Service
public class MarkServiceImpl implements com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.service.MarkService {

    @Autowired
    private ApplicationContext context;

    @Override
    public Rule getByName( String name ) {
        //        LambdaQueryWrapper<Mark> queryWrapper = new LambdaQueryWrapper<>();
        //        queryWrapper.eq(Mark::getName, name);
        //        Mark mark = getOne(queryWrapper);
        //        if (mark == null) {
        //            throw new RuntimeException("不存在：" + name);
        //        }
        //
        //        Long roleId = SecurityUtil.getCurrentRoleId();
        //        if (!roleService.isExist(roleId)) {
        //            throw new RuntimeException("角色不存在");
        //        }
        //
        //        // 角色 与 标记 的关联
        //        RoleMark roleMark = roleMarkService.getByRoleIdAndMarkId(roleId, mark.getId());
        //        if (roleMark == null) {
        //            throw new NoDataException("角色 与 标记 的关联为空，无访问权限");
        //        }
        //
        //        Rule rule = ruleService.getById(roleMark.getRuleId());
        //        if (rule == null) {
        //            throw new RuntimeException("不存在具体配置, Rule 为空");
        //        }

        return null;
    }

    @Override
    public DataScopeInfo execRuleByEntity( Rule entity ) {
        return execRuleHandler(entity);
    }

    private DataScopeInfo execRuleHandler( Rule rule ) {
        if (rule == null) {
            throw new RuntimeException("数据规则不存在");
        }
        RuleDto dto = new RuleDto();
        BeanUtil.copyProperties(rule, dto);
        DataScopeInfo info = new DataScopeInfo();
        info.setDto(dto);

        if (rule.getProvideType().equals(ProvideTypeEnum.VALUE.getCode())) {
            info.setDto(dto);
            return info;
        } else if (rule.getProvideType().equals(ProvideTypeEnum.METHOD.getCode())) {
            try {
                Class<?>[] paramsTypes = null;
                Object[] argValues = null;

                if (StrUtil.isNotBlank(rule.getFormalParam())
                        && StrUtil.isNotBlank(rule.getActualParam())) {
                    // 获取形参数组
                    String[] formalArray = rule.getFormalParam().split(";");
                    // 获取实参数组
                    String[] actualArray = rule.getActualParam().split(";");

                    if (formalArray.length != actualArray.length)
                        throw new RuntimeException("形参数量与实参数量不符合");

                    // 转换形参为Class数组
                    paramsTypes = new Class<?>[formalArray.length];
                    for (int i = 0; i < formalArray.length; i++) {
                        paramsTypes[i] = Class.forName(formalArray[i].trim());
                    }

                    // 转换实参为Object数组
                    argValues = new Object[actualArray.length];
                    for (int i = 0; i < actualArray.length; i++) {
                        argValues[i] = JSONObject.parseObject(actualArray[i], paramsTypes[i]);
                    }
                }

                Class<?> clazz = Class.forName(rule.getClassName());

                Method targetMethod = clazz.getDeclaredMethod(rule.getMethodName(), paramsTypes);
                if (Modifier.isStatic(targetMethod.getModifiers())) {
                    // 设置静态方法可访问
                    targetMethod.setAccessible(true);
                    // 执行静态方法
                    info.setIdList((List<Long>) targetMethod.invoke(null, argValues));
                    return info;
                } else {
                    try {
                        // 尝试从容器中获取实例
                        Object instance = context.getBean(Class.forName(rule.getClassName()));
                        Class<?> beanClazz = instance.getClass();
                        Method beanClazzMethod =
                                beanClazz.getDeclaredMethod(rule.getMethodName(), paramsTypes);

                        // 执行方法
                        info.setIdList(
                                (List<Long>)
                                        beanClazzMethod.invoke(
                                                instance, argValues)); // TODO (List<Long>)
                        // 这了如果封装成一个类可能更灵活：包含了各种信息在里面
                        return info;
                    } catch (NoSuchBeanDefinitionException e) {

                        // 创建类实例
                        Object obj = clazz.newInstance();
                        // 执行方法
                        info.setIdList((List<Long>) targetMethod.invoke(obj, argValues));
                        return info;
                    }
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("配置了不存在的方法");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("配置了不存在的类");
            } catch (Exception e) {
                LogUtils.error(e);
                throw new RuntimeException("其他错误：" + e.getMessage());
            }

        } else {
            throw new RuntimeException("错误的提供类型");
        }
    }

    @Override
    public DataScopeInfo execRuleByName( String name ) {
        Rule rule = getByName(name);
        return execRuleHandler(rule);
    }

    @Override
    public boolean existSameName( Long id, String scopeName ) {
        LambdaQueryWrapper<Mark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Mark::getName, scopeName);
        queryWrapper.ne(id != null, Mark::getId, id);
        //        return count(queryWrapper) > 0;
        return true;
    }

    @Override
    public PageData<MarkDto> page( MarkQuery query ) {
        //        Page<Mark> page = new Page<>(query.getCurPage(), query.getPageSize());
        //
        //        LambdaQueryWrapper<Mark> queryWrapper = new LambdaQueryWrapper<>();
        //        queryWrapper.orderByAsc(Mark::getSort);
        //        queryWrapper.like(StrUtil.isNotBlank(query.getName()), Mark::getName,
        // query.getName());
        //
        //        page(page, queryWrapper);
        //
        //        List<MarkDto> list = afterQueryHandler(page.getRecords());
        //        return new PageData<>(list, page.getTotal(), page.getPages());
        return null;
    }

    @Override
    public void enabledSwitch( Long id ) {
        //        Mark entity = getById(id);
        //        if (entity == null) {
        //            throw new RuntimeException("不存在");
        //        }
        //        entity.setEnabled(!entity.getEnabled());
        //        updateById(entity);
    }

    @Override
    public List<MarkDto> list( MarkQuery query ) {
        LambdaQueryWrapper<Mark> queryWrapper = new LambdaQueryWrapper<>();
        //        queryWrapper.like(StrUtil.isNotBlank(query.getName()), Mark::getName,
        // query.getName());
        queryWrapper.orderByAsc(Mark::getSort);
        //        return afterQueryHandler(list(queryWrapper));
        return null;
    }

    @Override
    public void removeAllByRoleIdAndMarkId( Long roleId, Long markId ) {
        if (roleId == null) {
            throw new RuntimeException("roleId不能为空");
        }
        if (markId == null) {
            throw new RuntimeException("markId不能为空");
        }

        LambdaQueryWrapper<RoleMark> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMark::getRoleId, roleId);
        queryWrapper.eq(RoleMark::getMarkId, markId);
        //        roleMarkService.remove(queryWrapper);
    }

    @Override
    public boolean addRelation( Long roleId, Long markId, Long ruleId ) {
        if (roleId == null) {
            throw new RuntimeException("roleId不能为空");
        }

        if (markId == null) {
            throw new RuntimeException("markId不能为空");
        }

        if (ruleId == null) {
            throw new RuntimeException("ruleId不能为空");
        }

        RoleMark entity = new RoleMark();
        entity.setRoleId(roleId);
        entity.setMarkId(markId);
        entity.setRuleId(ruleId);
        //        return roleMarkService.save(entity);
        return true;
    }

}
