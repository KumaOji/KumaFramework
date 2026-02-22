/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  org.springframework.security.core.GrantedAuthority
 *  org.springframework.security.oauth2.core.user.OAuth2User
 */
package com.kuma.boot.security.spring.authentication.login.social.oauth2client.weibo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kuma.boot.security.spring.utils.AuthorityUtils;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class WeiboOAuth2User
implements OAuth2User {
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String nameAttributeKey;
    @JsonProperty(value="id")
    private long id;
    @JsonProperty(value="idstr")
    private String idstr;
    @JsonProperty(value="screen_name")
    private String screenName;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="province")
    private String province;
    @JsonProperty(value="city")
    private String city;
    @JsonProperty(value="location")
    private String location;
    @JsonProperty(value="description")
    private String description;
    @JsonProperty(value="url")
    private String url;
    @JsonProperty(value="profile_image_url")
    private String profileImageUrl;
    @JsonProperty(value="profile_url")
    private String profileUrl;
    @JsonProperty(value="domain")
    private String domain;
    @JsonProperty(value="weihao")
    private String weihao;
    @JsonProperty(value="gender")
    private String gender;
    @JsonProperty(value="followers_count")
    private int followersCount;
    @JsonProperty(value="friends_count")
    private int friendsCount;
    @JsonProperty(value="statuses_count")
    private int statusesCount;
    @JsonProperty(value="favourites_count")
    private int favouritesCount;
    @JsonProperty(value="created_at")
    @JsonFormat(pattern="E MMM dd HH:mm:ss '+0800' yyyy", locale="en")
    private LocalDateTime createdAt;
    @JsonProperty(value="following")
    private boolean following;
    @JsonProperty(value="allow_all_act_msg")
    private boolean allowAllActMsg;
    @JsonProperty(value="geo_enabled")
    private boolean geoEnabled;
    @JsonProperty(value="verified")
    private boolean verified;
    @JsonProperty(value="verified_type")
    private int verifiedType;
    @JsonProperty(value="remark")
    private String remark;
    @JsonProperty(value="status")
    private Status status;
    @JsonProperty(value="allow_all_comment")
    private boolean allowAllComment;
    @JsonProperty(value="avatar_large")
    private String avatarLarge;
    @JsonProperty(value="avatar_hd")
    private String avatarHd;
    @JsonProperty(value="verified_reason")
    private String verifiedReason;
    @JsonProperty(value="follow_me")
    private boolean followMe;
    @JsonProperty(value="online_status")
    private int onlineStatus;
    @JsonProperty(value="bi_followers_count")
    private int biFollowersCount;
    @JsonProperty(value="lang")
    private String lang;
    @JsonProperty(value="is_teenager")
    private int isTeenager;
    @JsonProperty(value="vplus_ability")
    private int vplusAbility;
    @JsonProperty(value="like_me")
    private boolean likeMe;
    @JsonProperty(value="verified_contact_mobile")
    private String verifiedContactMobile;
    @JsonProperty(value="light_ring")
    private boolean lightRing;
    @JsonProperty(value="wenda_ability")
    private int wendaAbility;
    @JsonProperty(value="video_total_counter")
    private VideoTotalCounter videoTotalCounter;
    @JsonProperty(value="nft_ability")
    private int nftAbility;
    @JsonProperty(value="ecommerce_ability")
    private int ecommerceAbility;
    @JsonProperty(value="verified_contact_email")
    private String verifiedContactEmail;
    @JsonProperty(value="pay_date")
    private String payDate;
    @JsonProperty(value="credit_score")
    private int creditScore;
    @JsonProperty(value="user_ability_extend")
    private int userAbilityExtend;
    @JsonProperty(value="pay_remind")
    private int payRemind;
    @JsonProperty(value="brand_ability")
    private int brandAbility;
    @JsonProperty(value="super_topic_not_syn_count")
    private int superTopicNotSynCount;
    @JsonProperty(value="live_ability")
    private int liveAbility;
    @JsonProperty(value="cardid")
    private String cardid;
    @JsonProperty(value="is_teenager_list")
    private int isTeenagerList;
    @JsonProperty(value="video_status_count")
    private int videoStatusCount;
    @JsonProperty(value="newbrand_ability")
    private int newbrandAbility;
    @JsonProperty(value="verified_level")
    private int verifiedLevel;
    @JsonProperty(value="urisk")
    private int urisk;
    @JsonProperty(value="star")
    private int star;
    @JsonProperty(value="status_total_counter")
    private StatusTotalCounter statusTotalCounter;
    @JsonProperty(value="has_service_tel")
    private boolean hasServiceTel;
    @JsonProperty(value="block_app")
    private int blockApp;
    @JsonProperty(value="planet_video")
    private int planetVideo;
    @JsonProperty(value="gongyi_ability")
    private int gongyiAbility;
    @JsonProperty(value="hardfan_ability")
    private int hardfanAbility;
    @JsonProperty(value="insecurity")
    private Insecurity insecurity;
    @JsonProperty(value="verified_source")
    private String verifiedSource;
    @JsonProperty(value="urank")
    private int urank;
    @JsonProperty(value="verified_trade")
    private String verifiedTrade;
    @JsonProperty(value="green_mode")
    private int greenMode;
    @JsonProperty(value="mb_expire_time")
    private int mbExpireTime;
    @JsonProperty(value="verified_source_url")
    private String verifiedSourceUrl;
    @JsonProperty(value="video_mark")
    private int videoMark;
    @JsonProperty(value="live_status")
    private int liveStatus;
    @JsonProperty(value="special_follow")
    private boolean specialFollow;
    @JsonProperty(value="followers_count_str")
    private String followersCountStr;
    @JsonProperty(value="chaohua_ability")
    private int chaohuaAbility;
    @JsonProperty(value="like")
    private boolean like;
    @JsonProperty(value="verified_type_ext")
    private int verifiedTypeExt;
    @JsonProperty(value="pagefriends_count")
    private int pagefriendsCount;
    @JsonProperty(value="cover_image_phone")
    private String coverImagePhone;
    @JsonProperty(value="ptype")
    private int ptype;
    @JsonProperty(value="verified_reason_url")
    private String verifiedReasonUrl;
    @JsonProperty(value="block_word")
    private int blockWord;
    @JsonProperty(value="verified_state")
    private int verifiedState;
    @JsonProperty(value="avatar_type")
    private int avatarType;
    @JsonProperty(value="hongbaofei")
    private int hongbaofei;
    @JsonProperty(value="video_play_count")
    private int videoPlayCount;
    @JsonProperty(value="mbtype")
    private int mbtype;
    @JsonProperty(value="user_ability")
    private int userAbility;
    @JsonProperty(value="story_read_state")
    private int storyReadState;
    @JsonProperty(value="mbrank")
    private int mbrank;
    @JsonProperty(value="class")
    private int jsonMemberClass;
    @JsonProperty(value="pc_new")
    private int pcNew;
    @JsonProperty(value="paycolumn_ability")
    private int paycolumnAbility;
    @JsonProperty(value="brand_account")
    private int brandAccount;
    @JsonProperty(value="verified_contact_name")
    private String verifiedContactName;
    @JsonProperty(value="vclub_member")
    private int vclubMember;
    @JsonProperty(value="is_guardian")
    private int isGuardian;
    @JsonProperty(value="svip")
    private int svip;
    @JsonProperty(value="verified_reason_modified")
    private String verifiedReasonModified;
    private Integer block;
    @JsonProperty(value="block_me")
    private Integer blockMe;

    public String getName() {
        return this.idstr;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
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

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdstr() {
        return this.idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getScreenName() {
        return this.screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileImageUrl() {
        return this.profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileUrl() {
        return this.profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWeihao() {
        return this.weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowersCount() {
        return this.followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return this.friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getStatusesCount() {
        return this.statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public int getFavouritesCount() {
        return this.favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFollowing() {
        return this.following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isAllowAllActMsg() {
        return this.allowAllActMsg;
    }

    public void setAllowAllActMsg(boolean allowAllActMsg) {
        this.allowAllActMsg = allowAllActMsg;
    }

    public boolean isGeoEnabled() {
        return this.geoEnabled;
    }

    public void setGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getVerifiedType() {
        return this.verifiedType;
    }

    public void setVerifiedType(int verifiedType) {
        this.verifiedType = verifiedType;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isAllowAllComment() {
        return this.allowAllComment;
    }

    public void setAllowAllComment(boolean allowAllComment) {
        this.allowAllComment = allowAllComment;
    }

    public String getAvatarLarge() {
        return this.avatarLarge;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getAvatarHd() {
        return this.avatarHd;
    }

    public void setAvatarHd(String avatarHd) {
        this.avatarHd = avatarHd;
    }

    public String getVerifiedReason() {
        return this.verifiedReason;
    }

    public void setVerifiedReason(String verifiedReason) {
        this.verifiedReason = verifiedReason;
    }

    public boolean isFollowMe() {
        return this.followMe;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe = followMe;
    }

    public int getOnlineStatus() {
        return this.onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public int getBiFollowersCount() {
        return this.biFollowersCount;
    }

    public void setBiFollowersCount(int biFollowersCount) {
        this.biFollowersCount = biFollowersCount;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getIsTeenager() {
        return this.isTeenager;
    }

    public void setIsTeenager(int isTeenager) {
        this.isTeenager = isTeenager;
    }

    public int getVplusAbility() {
        return this.vplusAbility;
    }

    public void setVplusAbility(int vplusAbility) {
        this.vplusAbility = vplusAbility;
    }

    public boolean isLikeMe() {
        return this.likeMe;
    }

    public void setLikeMe(boolean likeMe) {
        this.likeMe = likeMe;
    }

    public String getVerifiedContactMobile() {
        return this.verifiedContactMobile;
    }

    public void setVerifiedContactMobile(String verifiedContactMobile) {
        this.verifiedContactMobile = verifiedContactMobile;
    }

    public boolean isLightRing() {
        return this.lightRing;
    }

    public void setLightRing(boolean lightRing) {
        this.lightRing = lightRing;
    }

    public int getWendaAbility() {
        return this.wendaAbility;
    }

    public void setWendaAbility(int wendaAbility) {
        this.wendaAbility = wendaAbility;
    }

    public VideoTotalCounter getVideoTotalCounter() {
        return this.videoTotalCounter;
    }

    public void setVideoTotalCounter(VideoTotalCounter videoTotalCounter) {
        this.videoTotalCounter = videoTotalCounter;
    }

    public int getNftAbility() {
        return this.nftAbility;
    }

    public void setNftAbility(int nftAbility) {
        this.nftAbility = nftAbility;
    }

    public int getEcommerceAbility() {
        return this.ecommerceAbility;
    }

    public void setEcommerceAbility(int ecommerceAbility) {
        this.ecommerceAbility = ecommerceAbility;
    }

    public String getVerifiedContactEmail() {
        return this.verifiedContactEmail;
    }

    public void setVerifiedContactEmail(String verifiedContactEmail) {
        this.verifiedContactEmail = verifiedContactEmail;
    }

    public String getPayDate() {
        return this.payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public int getCreditScore() {
        return this.creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getUserAbilityExtend() {
        return this.userAbilityExtend;
    }

    public void setUserAbilityExtend(int userAbilityExtend) {
        this.userAbilityExtend = userAbilityExtend;
    }

    public int getPayRemind() {
        return this.payRemind;
    }

    public void setPayRemind(int payRemind) {
        this.payRemind = payRemind;
    }

    public int getBrandAbility() {
        return this.brandAbility;
    }

    public void setBrandAbility(int brandAbility) {
        this.brandAbility = brandAbility;
    }

    public int getSuperTopicNotSynCount() {
        return this.superTopicNotSynCount;
    }

    public void setSuperTopicNotSynCount(int superTopicNotSynCount) {
        this.superTopicNotSynCount = superTopicNotSynCount;
    }

    public int getLiveAbility() {
        return this.liveAbility;
    }

    public void setLiveAbility(int liveAbility) {
        this.liveAbility = liveAbility;
    }

    public String getCardid() {
        return this.cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public int getIsTeenagerList() {
        return this.isTeenagerList;
    }

    public void setIsTeenagerList(int isTeenagerList) {
        this.isTeenagerList = isTeenagerList;
    }

    public int getVideoStatusCount() {
        return this.videoStatusCount;
    }

    public void setVideoStatusCount(int videoStatusCount) {
        this.videoStatusCount = videoStatusCount;
    }

    public int getNewbrandAbility() {
        return this.newbrandAbility;
    }

    public void setNewbrandAbility(int newbrandAbility) {
        this.newbrandAbility = newbrandAbility;
    }

    public int getVerifiedLevel() {
        return this.verifiedLevel;
    }

    public void setVerifiedLevel(int verifiedLevel) {
        this.verifiedLevel = verifiedLevel;
    }

    public int getUrisk() {
        return this.urisk;
    }

    public void setUrisk(int urisk) {
        this.urisk = urisk;
    }

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public StatusTotalCounter getStatusTotalCounter() {
        return this.statusTotalCounter;
    }

    public void setStatusTotalCounter(StatusTotalCounter statusTotalCounter) {
        this.statusTotalCounter = statusTotalCounter;
    }

    public boolean isHasServiceTel() {
        return this.hasServiceTel;
    }

    public void setHasServiceTel(boolean hasServiceTel) {
        this.hasServiceTel = hasServiceTel;
    }

    public int getBlockApp() {
        return this.blockApp;
    }

    public void setBlockApp(int blockApp) {
        this.blockApp = blockApp;
    }

    public int getPlanetVideo() {
        return this.planetVideo;
    }

    public void setPlanetVideo(int planetVideo) {
        this.planetVideo = planetVideo;
    }

    public int getGongyiAbility() {
        return this.gongyiAbility;
    }

    public void setGongyiAbility(int gongyiAbility) {
        this.gongyiAbility = gongyiAbility;
    }

    public int getHardfanAbility() {
        return this.hardfanAbility;
    }

    public void setHardfanAbility(int hardfanAbility) {
        this.hardfanAbility = hardfanAbility;
    }

    public Insecurity getInsecurity() {
        return this.insecurity;
    }

    public void setInsecurity(Insecurity insecurity) {
        this.insecurity = insecurity;
    }

    public String getVerifiedSource() {
        return this.verifiedSource;
    }

    public void setVerifiedSource(String verifiedSource) {
        this.verifiedSource = verifiedSource;
    }

    public int getUrank() {
        return this.urank;
    }

    public void setUrank(int urank) {
        this.urank = urank;
    }

    public String getVerifiedTrade() {
        return this.verifiedTrade;
    }

    public void setVerifiedTrade(String verifiedTrade) {
        this.verifiedTrade = verifiedTrade;
    }

    public int getGreenMode() {
        return this.greenMode;
    }

    public void setGreenMode(int greenMode) {
        this.greenMode = greenMode;
    }

    public int getMbExpireTime() {
        return this.mbExpireTime;
    }

    public void setMbExpireTime(int mbExpireTime) {
        this.mbExpireTime = mbExpireTime;
    }

    public String getVerifiedSourceUrl() {
        return this.verifiedSourceUrl;
    }

    public void setVerifiedSourceUrl(String verifiedSourceUrl) {
        this.verifiedSourceUrl = verifiedSourceUrl;
    }

    public int getVideoMark() {
        return this.videoMark;
    }

    public void setVideoMark(int videoMark) {
        this.videoMark = videoMark;
    }

    public int getLiveStatus() {
        return this.liveStatus;
    }

    public void setLiveStatus(int liveStatus) {
        this.liveStatus = liveStatus;
    }

    public boolean isSpecialFollow() {
        return this.specialFollow;
    }

    public void setSpecialFollow(boolean specialFollow) {
        this.specialFollow = specialFollow;
    }

    public String getFollowersCountStr() {
        return this.followersCountStr;
    }

    public void setFollowersCountStr(String followersCountStr) {
        this.followersCountStr = followersCountStr;
    }

    public int getChaohuaAbility() {
        return this.chaohuaAbility;
    }

    public void setChaohuaAbility(int chaohuaAbility) {
        this.chaohuaAbility = chaohuaAbility;
    }

    public boolean isLike() {
        return this.like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getVerifiedTypeExt() {
        return this.verifiedTypeExt;
    }

    public void setVerifiedTypeExt(int verifiedTypeExt) {
        this.verifiedTypeExt = verifiedTypeExt;
    }

    public int getPagefriendsCount() {
        return this.pagefriendsCount;
    }

    public void setPagefriendsCount(int pagefriendsCount) {
        this.pagefriendsCount = pagefriendsCount;
    }

    public String getCoverImagePhone() {
        return this.coverImagePhone;
    }

    public void setCoverImagePhone(String coverImagePhone) {
        this.coverImagePhone = coverImagePhone;
    }

    public int getPtype() {
        return this.ptype;
    }

    public void setPtype(int ptype) {
        this.ptype = ptype;
    }

    public String getVerifiedReasonUrl() {
        return this.verifiedReasonUrl;
    }

    public void setVerifiedReasonUrl(String verifiedReasonUrl) {
        this.verifiedReasonUrl = verifiedReasonUrl;
    }

    public int getBlockWord() {
        return this.blockWord;
    }

    public void setBlockWord(int blockWord) {
        this.blockWord = blockWord;
    }

    public int getVerifiedState() {
        return this.verifiedState;
    }

    public void setVerifiedState(int verifiedState) {
        this.verifiedState = verifiedState;
    }

    public int getAvatarType() {
        return this.avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public int getHongbaofei() {
        return this.hongbaofei;
    }

    public void setHongbaofei(int hongbaofei) {
        this.hongbaofei = hongbaofei;
    }

    public int getVideoPlayCount() {
        return this.videoPlayCount;
    }

    public void setVideoPlayCount(int videoPlayCount) {
        this.videoPlayCount = videoPlayCount;
    }

    public int getMbtype() {
        return this.mbtype;
    }

    public void setMbtype(int mbtype) {
        this.mbtype = mbtype;
    }

    public int getUserAbility() {
        return this.userAbility;
    }

    public void setUserAbility(int userAbility) {
        this.userAbility = userAbility;
    }

    public int getStoryReadState() {
        return this.storyReadState;
    }

    public void setStoryReadState(int storyReadState) {
        this.storyReadState = storyReadState;
    }

    public int getMbrank() {
        return this.mbrank;
    }

    public void setMbrank(int mbrank) {
        this.mbrank = mbrank;
    }

    public int getJsonMemberClass() {
        return this.jsonMemberClass;
    }

    public void setJsonMemberClass(int jsonMemberClass) {
        this.jsonMemberClass = jsonMemberClass;
    }

    public int getPcNew() {
        return this.pcNew;
    }

    public void setPcNew(int pcNew) {
        this.pcNew = pcNew;
    }

    public int getPaycolumnAbility() {
        return this.paycolumnAbility;
    }

    public void setPaycolumnAbility(int paycolumnAbility) {
        this.paycolumnAbility = paycolumnAbility;
    }

    public int getBrandAccount() {
        return this.brandAccount;
    }

    public void setBrandAccount(int brandAccount) {
        this.brandAccount = brandAccount;
    }

    public String getVerifiedContactName() {
        return this.verifiedContactName;
    }

    public void setVerifiedContactName(String verifiedContactName) {
        this.verifiedContactName = verifiedContactName;
    }

    public int getVclubMember() {
        return this.vclubMember;
    }

    public void setVclubMember(int vclubMember) {
        this.vclubMember = vclubMember;
    }

    public int getIsGuardian() {
        return this.isGuardian;
    }

    public void setIsGuardian(int isGuardian) {
        this.isGuardian = isGuardian;
    }

    public int getSvip() {
        return this.svip;
    }

    public void setSvip(int svip) {
        this.svip = svip;
    }

    public String getVerifiedReasonModified() {
        return this.verifiedReasonModified;
    }

    public void setVerifiedReasonModified(String verifiedReasonModified) {
        this.verifiedReasonModified = verifiedReasonModified;
    }

    public Integer getBlock() {
        return this.block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Integer getBlockMe() {
        return this.blockMe;
    }

    public void setBlockMe(Integer blockMe) {
        this.blockMe = blockMe;
    }

    public static class Status {
        private Integer version;
        private String picStatus;
        @JsonProperty(value="show_mlevel")
        private Integer showMlevel;
        @JsonFormat(pattern="E MMM dd HH:mm:ss '+0800' yyyy", locale="en")
        @JsonProperty(value="created_at")
        private LocalDateTime createdAt;
        @JsonProperty(value="id")
        private long id;
        @JsonProperty(value="mid")
        private String mid;
        @JsonProperty(value="idstr")
        private String idstr;
        @JsonProperty(value="text")
        private String text;
        @JsonProperty(value="source")
        private String source;
        @JsonProperty(value="favorited")
        private boolean favorited;
        @JsonProperty(value="truncated")
        private boolean truncated;
        @JsonProperty(value="in_reply_to_status_id")
        private String inReplyToStatusId;
        @JsonProperty(value="in_reply_to_user_id")
        private String inReplyToUserId;
        @JsonProperty(value="in_reply_to_screen_name")
        private String inReplyToScreenName;
        @JsonProperty(value="thumbnail_pic")
        private String thumbnailPic;
        @JsonProperty(value="bmiddle_pic")
        private String bmiddlePic;
        @JsonProperty(value="original_pic")
        private String originalPic;
        @JsonProperty(value="geo")
        private Object geo;
        @JsonProperty(value="reposts_count")
        private int repostsCount;
        @JsonProperty(value="comments_count")
        private int commentsCount;
        @JsonProperty(value="attitudes_count")
        private int attitudesCount;
        @JsonProperty(value="mlevel")
        private int mlevel;
        @JsonProperty(value="visible")
        private Visible visible;
        @JsonProperty(value="isLongText")
        private boolean isLongText;
        @JsonProperty(value="hot_weibo_tags")
        private List<Object> hotWeiboTags;
        @JsonProperty(value="annotations")
        private List<AnnotationsItem> annotations;
        @JsonProperty(value="mblogtype")
        private int mblogtype;
        @JsonProperty(value="rid")
        private String rid;
        @JsonProperty(value="positive_recom_flag")
        private int positiveRecomFlag;
        @JsonProperty(value="can_reprint")
        private boolean canReprint;
        @JsonProperty(value="is_show_bulletin")
        private int isShowBulletin;
        @JsonProperty(value="hide_flag")
        private int hideFlag;
        @JsonProperty(value="hasActionTypeCard")
        private int hasActionTypeCard;
        @JsonProperty(value="new_comment_style")
        private int newCommentStyle;
        @JsonProperty(value="mblog_vip_type")
        private int mblogVipType;
        @JsonProperty(value="content_auth")
        private int contentAuth;
        @JsonProperty(value="gif_ids")
        private String gifIds;
        @JsonProperty(value="source_type")
        private int sourceType;
        @JsonProperty(value="pic_urls")
        private List<PicUrlsItem> picUrls;
        @JsonProperty(value="biz_feature")
        private long bizFeature;
        @JsonProperty(value="userType")
        private int userType;
        @JsonProperty(value="text_tag_tips")
        private List<Object> textTagTips;
        @JsonProperty(value="darwin_tags")
        private List<Object> darwinTags;
        @JsonProperty(value="pending_approval_count")
        private int pendingApprovalCount;
        @JsonProperty(value="pic_num")
        private int picNum;
        @JsonProperty(value="is_paid")
        private boolean isPaid;
        @JsonProperty(value="reward_exhibition_type")
        private int rewardExhibitionType;
        @JsonProperty(value="reprint_cmt_count")
        private int reprintCmtCount;
        @JsonProperty(value="can_edit")
        private boolean canEdit;
        @JsonProperty(value="textLength")
        private int textLength;
        @JsonProperty(value="source_allowclick")
        private int sourceAllowclick;
        @JsonProperty(value="show_additional_indication")
        private int showAdditionalIndication;
        @JsonProperty(value="comment_manage_info")
        private CommentManageInfo commentManageInfo;
        @JsonProperty(value="more_info_type")
        private int moreInfoType;

        public Integer getVersion() {
            return this.version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getPicStatus() {
            return this.picStatus;
        }

        public void setPicStatus(String picStatus) {
            this.picStatus = picStatus;
        }

        public Integer getShowMlevel() {
            return this.showMlevel;
        }

        public void setShowMlevel(Integer showMlevel) {
            this.showMlevel = showMlevel;
        }

        public LocalDateTime getCreatedAt() {
            return this.createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public long getId() {
            return this.id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMid() {
            return this.mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getIdstr() {
            return this.idstr;
        }

        public void setIdstr(String idstr) {
            this.idstr = idstr;
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSource() {
            return this.source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public boolean isFavorited() {
            return this.favorited;
        }

        public void setFavorited(boolean favorited) {
            this.favorited = favorited;
        }

        public boolean isTruncated() {
            return this.truncated;
        }

        public void setTruncated(boolean truncated) {
            this.truncated = truncated;
        }

        public String getInReplyToStatusId() {
            return this.inReplyToStatusId;
        }

        public void setInReplyToStatusId(String inReplyToStatusId) {
            this.inReplyToStatusId = inReplyToStatusId;
        }

        public String getInReplyToUserId() {
            return this.inReplyToUserId;
        }

        public void setInReplyToUserId(String inReplyToUserId) {
            this.inReplyToUserId = inReplyToUserId;
        }

        public String getInReplyToScreenName() {
            return this.inReplyToScreenName;
        }

        public void setInReplyToScreenName(String inReplyToScreenName) {
            this.inReplyToScreenName = inReplyToScreenName;
        }

        public String getThumbnailPic() {
            return this.thumbnailPic;
        }

        public void setThumbnailPic(String thumbnailPic) {
            this.thumbnailPic = thumbnailPic;
        }

        public String getBmiddlePic() {
            return this.bmiddlePic;
        }

        public void setBmiddlePic(String bmiddlePic) {
            this.bmiddlePic = bmiddlePic;
        }

        public String getOriginalPic() {
            return this.originalPic;
        }

        public void setOriginalPic(String originalPic) {
            this.originalPic = originalPic;
        }

        public Object getGeo() {
            return this.geo;
        }

        public void setGeo(Object geo) {
            this.geo = geo;
        }

        public int getRepostsCount() {
            return this.repostsCount;
        }

        public void setRepostsCount(int repostsCount) {
            this.repostsCount = repostsCount;
        }

        public int getCommentsCount() {
            return this.commentsCount;
        }

        public void setCommentsCount(int commentsCount) {
            this.commentsCount = commentsCount;
        }

        public int getAttitudesCount() {
            return this.attitudesCount;
        }

        public void setAttitudesCount(int attitudesCount) {
            this.attitudesCount = attitudesCount;
        }

        public int getMlevel() {
            return this.mlevel;
        }

        public void setMlevel(int mlevel) {
            this.mlevel = mlevel;
        }

        public Visible getVisible() {
            return this.visible;
        }

        public void setVisible(Visible visible) {
            this.visible = visible;
        }

        public boolean isLongText() {
            return this.isLongText;
        }

        public void setLongText(boolean longText) {
            this.isLongText = longText;
        }

        public List<Object> getHotWeiboTags() {
            return this.hotWeiboTags;
        }

        public void setHotWeiboTags(List<Object> hotWeiboTags) {
            this.hotWeiboTags = hotWeiboTags;
        }

        public List<AnnotationsItem> getAnnotations() {
            return this.annotations;
        }

        public void setAnnotations(List<AnnotationsItem> annotations) {
            this.annotations = annotations;
        }

        public int getMblogtype() {
            return this.mblogtype;
        }

        public void setMblogtype(int mblogtype) {
            this.mblogtype = mblogtype;
        }

        public String getRid() {
            return this.rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public int getPositiveRecomFlag() {
            return this.positiveRecomFlag;
        }

        public void setPositiveRecomFlag(int positiveRecomFlag) {
            this.positiveRecomFlag = positiveRecomFlag;
        }

        public boolean isCanReprint() {
            return this.canReprint;
        }

        public void setCanReprint(boolean canReprint) {
            this.canReprint = canReprint;
        }

        public int getIsShowBulletin() {
            return this.isShowBulletin;
        }

        public void setIsShowBulletin(int isShowBulletin) {
            this.isShowBulletin = isShowBulletin;
        }

        public int getHideFlag() {
            return this.hideFlag;
        }

        public void setHideFlag(int hideFlag) {
            this.hideFlag = hideFlag;
        }

        public int getHasActionTypeCard() {
            return this.hasActionTypeCard;
        }

        public void setHasActionTypeCard(int hasActionTypeCard) {
            this.hasActionTypeCard = hasActionTypeCard;
        }

        public int getNewCommentStyle() {
            return this.newCommentStyle;
        }

        public void setNewCommentStyle(int newCommentStyle) {
            this.newCommentStyle = newCommentStyle;
        }

        public int getMblogVipType() {
            return this.mblogVipType;
        }

        public void setMblogVipType(int mblogVipType) {
            this.mblogVipType = mblogVipType;
        }

        public int getContentAuth() {
            return this.contentAuth;
        }

        public void setContentAuth(int contentAuth) {
            this.contentAuth = contentAuth;
        }

        public String getGifIds() {
            return this.gifIds;
        }

        public void setGifIds(String gifIds) {
            this.gifIds = gifIds;
        }

        public int getSourceType() {
            return this.sourceType;
        }

        public void setSourceType(int sourceType) {
            this.sourceType = sourceType;
        }

        public List<PicUrlsItem> getPicUrls() {
            return this.picUrls;
        }

        public void setPicUrls(List<PicUrlsItem> picUrls) {
            this.picUrls = picUrls;
        }

        public long getBizFeature() {
            return this.bizFeature;
        }

        public void setBizFeature(long bizFeature) {
            this.bizFeature = bizFeature;
        }

        public int getUserType() {
            return this.userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public List<Object> getTextTagTips() {
            return this.textTagTips;
        }

        public void setTextTagTips(List<Object> textTagTips) {
            this.textTagTips = textTagTips;
        }

        public List<Object> getDarwinTags() {
            return this.darwinTags;
        }

        public void setDarwinTags(List<Object> darwinTags) {
            this.darwinTags = darwinTags;
        }

        public int getPendingApprovalCount() {
            return this.pendingApprovalCount;
        }

        public void setPendingApprovalCount(int pendingApprovalCount) {
            this.pendingApprovalCount = pendingApprovalCount;
        }

        public int getPicNum() {
            return this.picNum;
        }

        public void setPicNum(int picNum) {
            this.picNum = picNum;
        }

        public boolean isPaid() {
            return this.isPaid;
        }

        public void setPaid(boolean paid) {
            this.isPaid = paid;
        }

        public int getRewardExhibitionType() {
            return this.rewardExhibitionType;
        }

        public void setRewardExhibitionType(int rewardExhibitionType) {
            this.rewardExhibitionType = rewardExhibitionType;
        }

        public int getReprintCmtCount() {
            return this.reprintCmtCount;
        }

        public void setReprintCmtCount(int reprintCmtCount) {
            this.reprintCmtCount = reprintCmtCount;
        }

        public boolean isCanEdit() {
            return this.canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public int getTextLength() {
            return this.textLength;
        }

        public void setTextLength(int textLength) {
            this.textLength = textLength;
        }

        public int getSourceAllowclick() {
            return this.sourceAllowclick;
        }

        public void setSourceAllowclick(int sourceAllowclick) {
            this.sourceAllowclick = sourceAllowclick;
        }

        public int getShowAdditionalIndication() {
            return this.showAdditionalIndication;
        }

        public void setShowAdditionalIndication(int showAdditionalIndication) {
            this.showAdditionalIndication = showAdditionalIndication;
        }

        public CommentManageInfo getCommentManageInfo() {
            return this.commentManageInfo;
        }

        public void setCommentManageInfo(CommentManageInfo commentManageInfo) {
            this.commentManageInfo = commentManageInfo;
        }

        public int getMoreInfoType() {
            return this.moreInfoType;
        }

        public void setMoreInfoType(int moreInfoType) {
            this.moreInfoType = moreInfoType;
        }
    }

    public static class VideoTotalCounter {
        @JsonProperty(value="play_cnt")
        private int playCnt;

        public int getPlayCnt() {
            return this.playCnt;
        }

        public void setPlayCnt(int playCnt) {
            this.playCnt = playCnt;
        }
    }

    public static class StatusTotalCounter {
        @JsonProperty(value="total_cnt")
        private int totalCnt;
        @JsonProperty(value="repost_cnt")
        private int repostCnt;
        @JsonProperty(value="comment_like_cnt")
        private int commentLikeCnt;
        @JsonProperty(value="like_cnt")
        private int likeCnt;
        @JsonProperty(value="comment_cnt")
        private int commentCnt;

        public int getTotalCnt() {
            return this.totalCnt;
        }

        public void setTotalCnt(int totalCnt) {
            this.totalCnt = totalCnt;
        }

        public int getRepostCnt() {
            return this.repostCnt;
        }

        public void setRepostCnt(int repostCnt) {
            this.repostCnt = repostCnt;
        }

        public int getCommentLikeCnt() {
            return this.commentLikeCnt;
        }

        public void setCommentLikeCnt(int commentLikeCnt) {
            this.commentLikeCnt = commentLikeCnt;
        }

        public int getLikeCnt() {
            return this.likeCnt;
        }

        public void setLikeCnt(int likeCnt) {
            this.likeCnt = likeCnt;
        }

        public int getCommentCnt() {
            return this.commentCnt;
        }

        public void setCommentCnt(int commentCnt) {
            this.commentCnt = commentCnt;
        }
    }

    public static class Insecurity {
        @JsonProperty(value="sexual_content")
        private boolean sexualContent;

        public boolean isSexualContent() {
            return this.sexualContent;
        }

        public void setSexualContent(boolean sexualContent) {
            this.sexualContent = sexualContent;
        }
    }

    public static class PicUrlsItem {
        @JsonProperty(value="thumbnail_pic")
        private String thumbnailPic;

        public String getThumbnailPic() {
            return this.thumbnailPic;
        }

        public void setThumbnailPic(String thumbnailPic) {
            this.thumbnailPic = thumbnailPic;
        }
    }

    public static class CommentManageInfo {
        @JsonProperty(value="comment_permission_type")
        private int commentPermissionType;
        @JsonProperty(value="comment_sort_type")
        private int commentSortType;
        @JsonProperty(value="approval_comment_type")
        private int approvalCommentType;

        public int getCommentPermissionType() {
            return this.commentPermissionType;
        }

        public void setCommentPermissionType(int commentPermissionType) {
            this.commentPermissionType = commentPermissionType;
        }

        public int getCommentSortType() {
            return this.commentSortType;
        }

        public void setCommentSortType(int commentSortType) {
            this.commentSortType = commentSortType;
        }

        public int getApprovalCommentType() {
            return this.approvalCommentType;
        }

        public void setApprovalCommentType(int approvalCommentType) {
            this.approvalCommentType = approvalCommentType;
        }
    }

    public static class AnnotationsItem {
        @JsonProperty(value="mapi_request")
        private boolean mapiRequest;
        @JsonProperty(value="client_mblogid")
        private String clientMblogid;
        @JsonProperty(value="photo_sub_type")
        private String photoSubType;

        public boolean isMapiRequest() {
            return this.mapiRequest;
        }

        public void setMapiRequest(boolean mapiRequest) {
            this.mapiRequest = mapiRequest;
        }

        public String getClientMblogid() {
            return this.clientMblogid;
        }

        public void setClientMblogid(String clientMblogid) {
            this.clientMblogid = clientMblogid;
        }

        public String getPhotoSubType() {
            return this.photoSubType;
        }

        public void setPhotoSubType(String photoSubType) {
            this.photoSubType = photoSubType;
        }
    }

    public static class Visible {
        @JsonProperty(value="list_id")
        private int listId;
        @JsonProperty(value="type")
        private int type;

        public int getListId() {
            return this.listId;
        }

        public void setListId(int listId) {
            this.listId = listId;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}

