/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class GithubOAuth2User
implements OAuth2User,
Serializable {
    private static final long serialVersionUID = 1L;
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    @JsonProperty(value="gists_url")
    private String gistsUrl;
    @JsonProperty(value="repos_url")
    private String reposUrl;
    @JsonProperty(value="following_url")
    private String followingUrl;
    @JsonProperty(value="twitter_username")
    private String twitterUsername;
    @JsonProperty(value="bio")
    private String bio;
    @JsonProperty(value="created_at")
    private String createdAt;
    @JsonProperty(value="login")
    private String login;
    @JsonProperty(value="type")
    private String type;
    @JsonProperty(value="blog")
    private String blog;
    @JsonProperty(value="subscriptions_url")
    private String subscriptionsUrl;
    @JsonProperty(value="updated_at")
    private String updatedAt;
    @JsonProperty(value="site_admin")
    private boolean siteAdmin;
    @JsonProperty(value="company")
    private String company;
    @JsonProperty(value="id")
    private int id;
    @JsonProperty(value="public_repos")
    private int publicRepos;
    @JsonProperty(value="gravatar_id")
    private String gravatarId;
    @JsonProperty(value="email")
    private String email;
    @JsonProperty(value="organizations_url")
    private String organizationsUrl;
    @JsonProperty(value="hireable")
    private boolean hireable;
    @JsonProperty(value="starred_url")
    private String starredUrl;
    @JsonProperty(value="followers_url")
    private String followersUrl;
    @JsonProperty(value="public_gists")
    private int publicGists;
    @JsonProperty(value="url")
    private String url;
    @JsonProperty(value="received_events_url")
    private String receivedEventsUrl;
    @JsonProperty(value="followers")
    private int followers;
    @JsonProperty(value="avatar_url")
    private String avatarUrl;
    @JsonProperty(value="events_url")
    private String eventsUrl;
    @JsonProperty(value="html_url")
    private String htmlUrl;
    @JsonProperty(value="following")
    private int following;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="location")
    private String location;
    @JsonProperty(value="node_id")
    private String nodeId;

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

    public String getGistsUrl() {
        return this.gistsUrl;
    }

    public void setGistsUrl(String gistsUrl) {
        this.gistsUrl = gistsUrl;
    }

    public String getReposUrl() {
        return this.reposUrl;
    }

    public void setReposUrl(String reposUrl) {
        this.reposUrl = reposUrl;
    }

    public String getFollowingUrl() {
        return this.followingUrl;
    }

    public void setFollowingUrl(String followingUrl) {
        this.followingUrl = followingUrl;
    }

    public String getTwitterUsername() {
        return this.twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getSubscriptionsUrl() {
        return this.subscriptionsUrl;
    }

    public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

    public String getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSiteAdmin() {
        return this.siteAdmin;
    }

    public void setSiteAdmin(boolean siteAdmin) {
        this.siteAdmin = siteAdmin;
    }

    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPublicRepos() {
        return this.publicRepos;
    }

    public void setPublicRepos(int publicRepos) {
        this.publicRepos = publicRepos;
    }

    public String getGravatarId() {
        return this.gravatarId;
    }

    public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganizationsUrl() {
        return this.organizationsUrl;
    }

    public void setOrganizationsUrl(String organizationsUrl) {
        this.organizationsUrl = organizationsUrl;
    }

    public boolean isHireable() {
        return this.hireable;
    }

    public void setHireable(boolean hireable) {
        this.hireable = hireable;
    }

    public String getStarredUrl() {
        return this.starredUrl;
    }

    public void setStarredUrl(String starredUrl) {
        this.starredUrl = starredUrl;
    }

    public String getFollowersUrl() {
        return this.followersUrl;
    }

    public void setFollowersUrl(String followersUrl) {
        this.followersUrl = followersUrl;
    }

    public int getPublicGists() {
        return this.publicGists;
    }

    public void setPublicGists(int publicGists) {
        this.publicGists = publicGists;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReceivedEventsUrl() {
        return this.receivedEventsUrl;
    }

    public void setReceivedEventsUrl(String receivedEventsUrl) {
        this.receivedEventsUrl = receivedEventsUrl;
    }

    public int getFollowers() {
        return this.followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEventsUrl() {
        return this.eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getHtmlUrl() {
        return this.htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public int getFollowing() {
        return this.following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}

