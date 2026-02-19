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

package com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model;

import java.util.List;

/**
 * RoleDto
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RoleDto extends com.kuma.boot.data.mybatis.mybatisplus.interceptor.datascopeother.model.EntityDto {

    //    @ApiModelProperty("名称")
    //    @NotBlank(message = "名称不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private String name;

    //    @ApiModelProperty("描述")
    private String description;
    //
    //    @ApiModelProperty("编码")
    //    @NotBlank(message = "编码不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    private String code;

    //    @ApiModelProperty("菜单ids")
    //    @JsonSerialize(using = ListLongSerializer.class)
    private List<Long> menuIds;

    //    @ApiModelProperty("是否启用")
    private Boolean enabled;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode( String code ) {
        this.code = code;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds( List<Long> menuIds ) {
        this.menuIds = menuIds;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled( Boolean enabled ) {
        this.enabled = enabled;
    }
}
