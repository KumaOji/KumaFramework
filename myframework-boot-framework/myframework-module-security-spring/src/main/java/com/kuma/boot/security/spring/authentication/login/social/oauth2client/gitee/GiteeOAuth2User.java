/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.gitee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GiteeOAuth2User
implements OAuth2User,
Serializable {
    private static final long serialVersionUID = 1L;
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    private Integer id;
    private String login;
    private String name;
    @JsonProperty(value="avatar_url")
    private String avatarUrl;
    private String url;
    @JsonProperty(value="html_url")
    private String htmlUrl;
    @JsonProperty(value="followers_url")
    private String followersUrl;
    @JsonProperty(value="following_url")
    private String followingUrl;
    @JsonProperty(value="gists_url")
    private String gistsUrl;
    @JsonProperty(value="starred_url")
    private String starredUrl;
    @JsonProperty(value="subscriptions_url")
    private String subscriptionsUrl;
    @JsonProperty(value="organizations_url")
    private String organizationsUrl;
    @JsonProperty(value="repos_url")
    private String reposUrl;
    @JsonProperty(value="events_url")
    private String eventsUrl;
    @JsonProperty(value="received_events_url")
    private String receivedEventsUrl;
    private String type;
    private String blog;
    private String weibo;
    private String bio;
    @JsonProperty(value="public_repos")
    private Integer publicRepos;
    @JsonProperty(value="public_gists")
    private Integer publicGists;
    private Integer followers;
    private Integer following;
    private Integer stared;
    private Integer watched;
    @JsonProperty(value="created_at")
    private Date createdAt;
    @JsonProperty(value="updated_at")
    private Date updatedAt;
    private String email;

    public String getName() {
        return this.nameAttributeKey;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getNameAttributeKey() {
        return this.nameAttributeKey;
    }

    public void setNameAttributeKey(String nameAttributeKey) {
        this.nameAttributeKey = nameAttributeKey;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlUrl() {
        return this.htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getFollowersUrl() {
        return this.followersUrl;
    }

    public void setFollowersUrl(String followersUrl) {
        this.followersUrl = followersUrl;
    }

    public String getFollowingUrl() {
        return this.followingUrl;
    }

    public void setFollowingUrl(String followingUrl) {
        this.followingUrl = followingUrl;
    }

    public String getGistsUrl() {
        return this.gistsUrl;
    }

    public void setGistsUrl(String gistsUrl) {
        this.gistsUrl = gistsUrl;
    }

    public String getStarredUrl() {
        return this.starredUrl;
    }

    public void setStarredUrl(String starredUrl) {
        this.starredUrl = starredUrl;
    }

    public String getSubscriptionsUrl() {
        return this.subscriptionsUrl;
    }

    public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

    public String getOrganizationsUrl() {
        return this.organizationsUrl;
    }

    public void setOrganizationsUrl(String organizationsUrl) {
        this.organizationsUrl = organizationsUrl;
    }

    public String getReposUrl() {
        return this.reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getEventsUrl() {
        return this.eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getReceivedEventsUrl() {
        return this.receivedEventsUrl;
    }

    public void setReceivedEventsUrl(String receivedEventsUrl) {
        this.receivedEventsUrl = receivedEventsUrl;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBlog() {
        return this.blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getWeibo() {
        return this.weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getPublicRepos() {
        return this.publicRepos;
    }

    public void setPublicRepos(Integer publicRepos) {
        this.publicRepos = publicRepos;
    }

    public Integer getPublicGists() {
        return this.publicGists;
    }

    public void setPublicGists(Integer publicGists) {
        this.publicGists = publicGists;
    }

    public Integer getFollowers() {
        return this.followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return this.following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Integer getStared() {
        return this.stared;
    }

    public void setStared(Integer stared) {
        this.stared = stared;
    }

    public Integer getWatched() {
        return this.watched;
    }

    public void setWatched(Integer watched) {
        this.watched = watched;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

