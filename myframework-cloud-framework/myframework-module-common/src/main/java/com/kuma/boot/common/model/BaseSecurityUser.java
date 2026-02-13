/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.kuma.boot.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;

public class BaseSecurityUser
implements Serializable,
Cloneable {
    private static final long serialVersionUID = -3685249101751401211L;
    public static final String ROLE_PREFIX = "ROLE_";
    private Long userId;
    private String account;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String mobile;
    private String email;
    private Integer sex;
    private String birthday;
    private String avatar;
    private Integer status;
    private String lockFlag;
    private String delFlag;
    private Integer type;
    private List<String> orgIds;
    private Map<String, List<String>> deptIds;
    private Map<String, List<String>> positionIds;
    private Set<String> roleIds;
    private Set<String> roleCodes;
    private Set<String> permissions;
    private List<String> dataScopes;
    private String tenant;
    private boolean tenantSuperAdmin;
    private boolean tenantAdmin;
    private boolean admin;
    private Long storeId;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    public BaseSecurityUser() {
    }

    public BaseSecurityUser(Long userId, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Set<String> permissions, Set<String> roleCodes) {
        Assert.isTrue((username != null && !username.isEmpty() && password != null ? 1 : 0) != 0, (String)"Cannot pass null or empty values to constructor");
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
        return this.storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLockFlag() {
        return this.lockFlag;
    }

    public void setLockFlag(String lockFlag) {
        this.lockFlag = lockFlag;
    }

    public String getDelFlag() {
        return this.delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Set<String> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<String> getRoleIds() {
        return this.roleIds;
    }

    public void setRoleIds(Set<String> roleIds) {
        this.roleIds = roleIds;
    }

    public Set<String> getRoleCodes() {
        return this.roleCodes;
    }

    public void setRoleCodes(Set<String> roleCodes) {
        this.roleCodes = roleCodes;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public boolean getAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Map<String, List<String>> getDeptIds() {
        return this.deptIds;
    }

    public void setDeptIds(Map<String, List<String>> deptIds) {
        this.deptIds = deptIds;
    }

    public Map<String, List<String>> getPositionIds() {
        return this.positionIds;
    }

    public void setPositionIds(Map<String, List<String>> positionIds) {
        this.positionIds = positionIds;
    }

    public boolean isTenantSuperAdmin() {
        return this.tenantSuperAdmin;
    }

    public void setTenantSuperAdmin(boolean tenantSuperAdmin) {
        this.tenantSuperAdmin = tenantSuperAdmin;
    }

    public boolean isTenantAdmin() {
        return this.tenantAdmin;
    }

    public void setTenantAdmin(boolean tenantAdmin) {
        this.tenantAdmin = tenantAdmin;
    }

    public List<String> getOrgIds() {
        return this.orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }

    public List<String> getDataScopes() {
        return this.dataScopes;
    }

    public void setDataScopes(List<String> dataScopes) {
        this.dataScopes = dataScopes;
    }

    public boolean getLongTerm() {
        return false;
    }

    public boolean getAccountNonExpired() {
        return this.accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean getAccountNonLocked() {
        return this.accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean getCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static BaseSecurityUserBuilder builderUser() {
        return new BaseSecurityUserBuilder();
    }

    public static final class BaseSecurityUserBuilder {
        private final BaseSecurityUser baseSecurityUser = new BaseSecurityUser();

        private BaseSecurityUserBuilder() {
        }

        public BaseSecurityUserBuilder userId(Long userId) {
            this.baseSecurityUser.setUserId(userId);
            return this;
        }

        public BaseSecurityUserBuilder account(String account) {
            this.baseSecurityUser.setAccount(account);
            return this;
        }

        public BaseSecurityUserBuilder username(String username) {
            this.baseSecurityUser.setUsername(username);
            return this;
        }

        public BaseSecurityUserBuilder nickname(String nickname) {
            this.baseSecurityUser.setNickname(nickname);
            return this;
        }

        public BaseSecurityUserBuilder password(String password) {
            this.baseSecurityUser.setPassword(password);
            return this;
        }

        public BaseSecurityUserBuilder phone(String phone) {
            this.baseSecurityUser.setPhone(phone);
            return this;
        }

        public BaseSecurityUserBuilder mobile(String mobile) {
            this.baseSecurityUser.setMobile(mobile);
            return this;
        }

        public BaseSecurityUserBuilder email(String email) {
            this.baseSecurityUser.setEmail(email);
            return this;
        }

        public BaseSecurityUserBuilder sex(Integer sex) {
            this.baseSecurityUser.setSex(sex);
            return this;
        }

        public BaseSecurityUserBuilder birthday(String birthday) {
            this.baseSecurityUser.setBirthday(birthday);
            return this;
        }

        public BaseSecurityUserBuilder avatar(String avatar) {
            this.baseSecurityUser.setAvatar(avatar);
            return this;
        }

        public BaseSecurityUserBuilder status(Integer status) {
            this.baseSecurityUser.setStatus(status);
            return this;
        }

        public BaseSecurityUserBuilder lockFlag(String lockFlag) {
            this.baseSecurityUser.setLockFlag(lockFlag);
            return this;
        }

        public BaseSecurityUserBuilder delFlag(String delFlag) {
            this.baseSecurityUser.setDelFlag(delFlag);
            return this;
        }

        public BaseSecurityUserBuilder type(Integer type) {
            this.baseSecurityUser.setType(type);
            return this;
        }

        public BaseSecurityUserBuilder orgIds(List<String> orgIds) {
            this.baseSecurityUser.setOrgIds(orgIds);
            return this;
        }

        public BaseSecurityUserBuilder deptIds(Map<String, List<String>> deptIds) {
            this.baseSecurityUser.setDeptIds(deptIds);
            return this;
        }

        public BaseSecurityUserBuilder positionIds(Map<String, List<String>> positionIds) {
            this.baseSecurityUser.setPositionIds(positionIds);
            return this;
        }

        public BaseSecurityUserBuilder roleIds(Set<String> roleIds) {
            this.baseSecurityUser.setRoleIds(roleIds);
            return this;
        }

        public BaseSecurityUserBuilder roleCodes(Set<String> roleCodes) {
            this.baseSecurityUser.setRoleCodes(roleCodes);
            return this;
        }

        public BaseSecurityUserBuilder permissions(Set<String> permissions) {
            this.baseSecurityUser.setPermissions(permissions);
            return this;
        }

        public BaseSecurityUserBuilder dataScopes(List<String> dataScopes) {
            this.baseSecurityUser.setDataScopes(dataScopes);
            return this;
        }

        public BaseSecurityUserBuilder tenant(String tenant) {
            this.baseSecurityUser.setTenant(tenant);
            return this;
        }

        public BaseSecurityUserBuilder tenantSuperAdmin(boolean tenantSuperAdmin) {
            this.baseSecurityUser.setTenantSuperAdmin(tenantSuperAdmin);
            return this;
        }

        public BaseSecurityUserBuilder tenantAdmin(boolean tenantAdmin) {
            this.baseSecurityUser.setTenantAdmin(tenantAdmin);
            return this;
        }

        public BaseSecurityUserBuilder admin(boolean admin) {
            this.baseSecurityUser.setAdmin(admin);
            return this;
        }

        public BaseSecurityUserBuilder storeId(Long storeId) {
            this.baseSecurityUser.setStoreId(storeId);
            return this;
        }

        public BaseSecurityUserBuilder enabled(boolean enabled) {
            this.baseSecurityUser.setEnabled(enabled);
            return this;
        }

        public BaseSecurityUserBuilder credentialsNonExpired(boolean credentialsNonExpired) {
            this.baseSecurityUser.setCredentialsNonExpired(credentialsNonExpired);
            return this;
        }

        public BaseSecurityUserBuilder accountNonLocked(boolean accountNonLocked) {
            this.baseSecurityUser.setAccountNonLocked(accountNonLocked);
            return this;
        }

        public BaseSecurityUserBuilder accountNonExpired(boolean accountNonExpired) {
            this.baseSecurityUser.setAccountNonExpired(accountNonExpired);
            return this;
        }

        public BaseSecurityUser build() {
            return this.baseSecurityUser;
        }
    }
}

