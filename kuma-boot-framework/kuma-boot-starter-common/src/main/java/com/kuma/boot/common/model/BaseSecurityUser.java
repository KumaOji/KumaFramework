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

package com.kuma.boot.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户手机号和账号密码 身份权限认证类 登陆身份认证
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:17:12
 */
public class BaseSecurityUser implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -3685249101751401211L;

    /**
     * ROLE_PREFIX
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * email
     */
    private String email;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 1-启用，2-禁用
     */
    private Integer status;

    /**
     * 是否锁定
     */
    private String lockFlag;

    /**
     * 是否删除
     */
    private String delFlag;

    /**
     * type 1.平台用户 2.商户用户(个人用户/企业用户)
     */
    private Integer type;

    // ************************组织(公司)信息******************************
    /**
     * 公司id列表 (用户可能在多个公司)
     */
    private List<String> orgIds;

    /**
     * 公司id -> 部门id列表 (用户可能在多个公司下多个部门)
     */
    private Map<String, List<String>> deptIds;

    /**
     * 部门id -> 岗位id列表 (用户可能在多个公司下多个岗位)
     */
    private Map<String, List<String>> positionIds;

    // ************************角色信息******************************
    /**
     * 角色id
     */
    private Set<String> roleIds;

    /**
     * 角色code
     */
    private Set<String> roleCodes;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    // ************************数据权限信息******************************

    private List<String> dataScopes;

    // *****************************租户信息********************
    /**
     * 租户标识
     */
    private String tenant;

    /**
     * 租户超级管理员 (看公司下面所有的信息)
     */
    private boolean tenantSuperAdmin;

    /**
     * 租户下每一个公司的管理员 (只看租户下面某一个子公司数据)
     */
    private boolean tenantAdmin;

    /**
     * 超级超级人员 (开发人员 所有的租户数据都可以看)
     */
    private boolean admin;

    /**
     * 商店id type=2有值
     */
    private Long storeId;

    /**
     * 账户不过期
     */
    private boolean accountNonExpired;

    /**
     * 非锁定账户
     */
    private boolean accountNonLocked;

    /**
     * 凭证不过期
     */
    private boolean credentialsNonExpired;

    /**
     * 启用
     */
    private boolean enabled;

    public BaseSecurityUser() {
    }

    /**
     * SecurityUser
     *
     * @param userId 用户Id
     * @param username 用户名称
     * @param password 密码
     * @param permissions 权限
     * @param roleCodes 权限code
     * @since 2021-09-02 19:18:58
     */
    public BaseSecurityUser(
            Long userId,
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Set<String> permissions,
            Set<String> roleCodes ) {
        if (!(username != null && !username.isEmpty() && password != null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.permissions = permissions;
        this.roleCodes = roleCodes;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId( Long storeId ) {
        this.storeId = storeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId( Long userId ) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone( String phone ) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar( String avatar ) {
        this.avatar = avatar;
    }

    public String getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag( String lockFlag ) {
        this.lockFlag = lockFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag( String delFlag ) {
        this.delFlag = delFlag;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname( String nickname ) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex( Integer sex ) {
        this.sex = sex;
    }

    public Integer getType() {
        return type;
    }

    public void setType( Integer type ) {
        this.type = type;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions( Set<String> permissions ) {
        this.permissions = permissions;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount( String account ) {
        this.account = account;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile( String mobile ) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday( String birthday ) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus( Integer status ) {
        this.status = status;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds( Set<String> roleIds ) {
        this.roleIds = roleIds;
    }

    public Set<String> getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes( Set<String> roleCodes ) {
        this.roleCodes = roleCodes;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin( boolean admin ) {
        this.admin = admin;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant( String tenant ) {
        this.tenant = tenant;
    }

    public Map<String, List<String>> getDeptIds() {
        return deptIds;
    }

    public void setDeptIds( Map<String, List<String>> deptIds ) {
        this.deptIds = deptIds;
    }

    public Map<String, List<String>> getPositionIds() {
        return positionIds;
    }

    public void setPositionIds( Map<String, List<String>> positionIds ) {
        this.positionIds = positionIds;
    }

    public boolean isTenantSuperAdmin() {
        return tenantSuperAdmin;
    }

    public void setTenantSuperAdmin( boolean tenantSuperAdmin ) {
        this.tenantSuperAdmin = tenantSuperAdmin;
    }

    public boolean isTenantAdmin() {
        return tenantAdmin;
    }

    public void setTenantAdmin( boolean tenantAdmin ) {
        this.tenantAdmin = tenantAdmin;
    }

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds( List<String> orgIds ) {
        this.orgIds = orgIds;
    }

    public List<String> getDataScopes() {
        return dataScopes;
    }

    public void setDataScopes( List<String> dataScopes ) {
        this.dataScopes = dataScopes;
    }

    // 获取是否长期有效的token 此函数可删
    public boolean getLongTerm() {
        return false;
    }

    public boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired( boolean accountNonExpired ) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked( boolean accountNonLocked ) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired( boolean credentialsNonExpired ) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    public static BaseSecurityUserBuilder builderUser() {
        return new BaseSecurityUserBuilder();
    }

    /**
     * BaseSecurityUserBuilder
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-17 10:30:45
     */
    public static final class BaseSecurityUserBuilder {

        private final BaseSecurityUser baseSecurityUser;

        private BaseSecurityUserBuilder() {
            baseSecurityUser = new BaseSecurityUser();
        }

        public BaseSecurityUserBuilder userId( Long userId ) {
            baseSecurityUser.setUserId(userId);
            return this;
        }

        public BaseSecurityUserBuilder account( String account ) {
            baseSecurityUser.setAccount(account);
            return this;
        }

        public BaseSecurityUserBuilder username( String username ) {
            baseSecurityUser.setUsername(username);
            return this;
        }

        public BaseSecurityUserBuilder nickname( String nickname ) {
            baseSecurityUser.setNickname(nickname);
            return this;
        }

        public BaseSecurityUserBuilder password( String password ) {
            baseSecurityUser.setPassword(password);
            return this;
        }

        public BaseSecurityUserBuilder phone( String phone ) {
            baseSecurityUser.setPhone(phone);
            return this;
        }

        public BaseSecurityUserBuilder mobile( String mobile ) {
            baseSecurityUser.setMobile(mobile);
            return this;
        }

        public BaseSecurityUserBuilder email( String email ) {
            baseSecurityUser.setEmail(email);
            return this;
        }

        public BaseSecurityUserBuilder sex( Integer sex ) {
            baseSecurityUser.setSex(sex);
            return this;
        }

        public BaseSecurityUserBuilder birthday( String birthday ) {
            baseSecurityUser.setBirthday(birthday);
            return this;
        }

        public BaseSecurityUserBuilder avatar( String avatar ) {
            baseSecurityUser.setAvatar(avatar);
            return this;
        }

        public BaseSecurityUserBuilder status( Integer status ) {
            baseSecurityUser.setStatus(status);
            return this;
        }

        public BaseSecurityUserBuilder lockFlag( String lockFlag ) {
            baseSecurityUser.setLockFlag(lockFlag);
            return this;
        }

        public BaseSecurityUserBuilder delFlag( String delFlag ) {
            baseSecurityUser.setDelFlag(delFlag);
            return this;
        }

        public BaseSecurityUserBuilder type( Integer type ) {
            baseSecurityUser.setType(type);
            return this;
        }

        public BaseSecurityUserBuilder orgIds( List<String> orgIds ) {
            baseSecurityUser.setOrgIds(orgIds);
            return this;
        }

        public BaseSecurityUserBuilder deptIds( Map<String, List<String>> deptIds ) {
            baseSecurityUser.setDeptIds(deptIds);
            return this;
        }

        public BaseSecurityUserBuilder positionIds( Map<String, List<String>> positionIds ) {
            baseSecurityUser.setPositionIds(positionIds);
            return this;
        }

        public BaseSecurityUserBuilder roleIds( Set<String> roleIds ) {
            baseSecurityUser.setRoleIds(roleIds);
            return this;
        }

        public BaseSecurityUserBuilder roleCodes( Set<String> roleCodes ) {
            baseSecurityUser.setRoleCodes(roleCodes);
            return this;
        }

        public BaseSecurityUserBuilder permissions( Set<String> permissions ) {
            baseSecurityUser.setPermissions(permissions);
            return this;
        }

        public BaseSecurityUserBuilder dataScopes( List<String> dataScopes ) {
            baseSecurityUser.setDataScopes(dataScopes);
            return this;
        }

        public BaseSecurityUserBuilder tenant( String tenant ) {
            baseSecurityUser.setTenant(tenant);
            return this;
        }

        public BaseSecurityUserBuilder tenantSuperAdmin( boolean tenantSuperAdmin ) {
            baseSecurityUser.setTenantSuperAdmin(tenantSuperAdmin);
            return this;
        }

        public BaseSecurityUserBuilder tenantAdmin( boolean tenantAdmin ) {
            baseSecurityUser.setTenantAdmin(tenantAdmin);
            return this;
        }

        public BaseSecurityUserBuilder admin( boolean admin ) {
            baseSecurityUser.setAdmin(admin);
            return this;
        }

        public BaseSecurityUserBuilder storeId( Long storeId ) {
            baseSecurityUser.setStoreId(storeId);
            return this;
        }

        public BaseSecurityUserBuilder enabled( boolean enabled ) {
            baseSecurityUser.setEnabled(enabled);
            return this;
        }

        public BaseSecurityUserBuilder credentialsNonExpired( boolean credentialsNonExpired ) {
            baseSecurityUser.setCredentialsNonExpired(credentialsNonExpired);
            return this;
        }

        public BaseSecurityUserBuilder accountNonLocked( boolean accountNonLocked ) {
            baseSecurityUser.setAccountNonLocked(accountNonLocked);
            return this;
        }

        public BaseSecurityUserBuilder accountNonExpired( boolean accountNonExpired ) {
            baseSecurityUser.setAccountNonExpired(accountNonExpired);
            return this;
        }

        public BaseSecurityUser build() {
            return baseSecurityUser;
        }
    }
}
