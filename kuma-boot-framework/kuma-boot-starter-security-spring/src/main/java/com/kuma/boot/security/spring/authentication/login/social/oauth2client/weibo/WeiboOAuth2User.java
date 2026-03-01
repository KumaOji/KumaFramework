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

public class WeiboOAuth2User implements OAuth2User {
    // 统一赋予USER角色
    private Set<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
    private Map<String, Object> attributes = new HashMap<>();
    private String nameAttributeKey;

    /**
     * 用户UID
     */
    @JsonProperty("id")
    private long id;

    /**
     * 字符串型的用户UID
     */
    @JsonProperty("idstr")
    private String idstr;

    /**
     * 用户昵称
     */
    @JsonProperty("screen_name")
    private String screenName;

    /**
     * 友好显示名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 用户所在省级ID
     */
    @JsonProperty("province")
    private String province;

    /**
     * 用户所在城市ID
     */
    @JsonProperty("city")
    private String city;

    /**
     * 用户所在地
     */
    @JsonProperty("location")
    private String location;

    /**
     * 用户个人描述
     */
    @JsonProperty("description")
    private String description;

    /**
     * 用户博客地址
     */
    @JsonProperty("url")
    private String url;

    /**
     * 用户头像地址（中图），50×50像素
     */
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    /**
     * 用户的微博统一URL地址
     */
    @JsonProperty("profile_url")
    private String profileUrl;

    /**
     * 用户的个性化域名
     */
    @JsonProperty("domain")
    private String domain;

    /**
     * 用户的微号
     */
    @JsonProperty("weihao")
    private String weihao;

    /**
     * 性别，m：男、f：女、n：未知
     */
    @JsonProperty("gender")
    private String gender;

    /**
     * 粉丝数
     */
    @JsonProperty("followers_count")
    private int followersCount;

    /**
     * 关注数
     */
    @JsonProperty("friends_count")
    private int friendsCount;

    /**
     * 微博数
     */
    @JsonProperty("statuses_count")
    private int statusesCount;

    /**
     * 收藏数
     */
    @JsonProperty("favourites_count")
    private int favouritesCount;

    /**
     * 用户创建（注册）时间
     */
    @JsonProperty("created_at")
    @JsonFormat(pattern = "E MMM dd HH:mm:ss '+0800' yyyy", locale = "en")
    private LocalDateTime createdAt;

    /**
     * 暂未支持
     */
    @JsonProperty("following")
    private boolean following;

    /**
     * 是否允许所有人给我发私信，true：是，false：否
     */
    @JsonProperty("allow_all_act_msg")
    private boolean allowAllActMsg;

    /**
     * 是否允许标识用户的地理位置，true：是，false：否
     */
    @JsonProperty("geo_enabled")
    private boolean geoEnabled;

    /**
     * 是否是微博认证用户，即加V用户，true：是，false：否
     */
    @JsonProperty("verified")
    private boolean verified;

    /**
     * 暂未支持
     */
    @JsonProperty("verified_type")
    private int verifiedType;

    /**
     * 用户备注信息，只有在查询用户关系时才返回此字段
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 用户的最近一条微博信息字段
     */
    @JsonProperty("status")
    private Status status;

    /**
     * 是否允许所有人对我的微博进行评论，true：是，false：否
     */
    @JsonProperty("allow_all_comment")
    private boolean allowAllComment;

    /**
     * 用户头像地址（大图），180×180像素
     */
    @JsonProperty("avatar_large")
    private String avatarLarge;

    /**
     * 用户头像地址（高清），高清头像原图
     */
    @JsonProperty("avatar_hd")
    private String avatarHd;

    /**
     * 认证原因
     */
    @JsonProperty("verified_reason")
    private String verifiedReason;

    /**
     * 该用户是否关注当前登录用户，true：是，false：否
     */
    @JsonProperty("follow_me")
    private boolean followMe;

    /**
     * 用户的在线状态，0：不在线、1：在线
     */
    @JsonProperty("online_status")
    private int onlineStatus;

    /**
     * 用户的互粉数
     */
    @JsonProperty("bi_followers_count")
    private int biFollowersCount;

    /**
     * 用户当前的语言版本，zh-cn：简体中文，zh-tw：繁体中文，en：英语
     */
    @JsonProperty("lang")
    private String lang;

    @JsonProperty("is_teenager")
    private int isTeenager;

    @JsonProperty("vplus_ability")
    private int vplusAbility;

    @JsonProperty("like_me")
    private boolean likeMe;

    @JsonProperty("verified_contact_mobile")
    private String verifiedContactMobile;

    @JsonProperty("light_ring")
    private boolean lightRing;

    @JsonProperty("wenda_ability")
    private int wendaAbility;

    @JsonProperty("video_total_counter")
    private VideoTotalCounter videoTotalCounter;

    @JsonProperty("nft_ability")
    private int nftAbility;

    @JsonProperty("ecommerce_ability")
    private int ecommerceAbility;

    @JsonProperty("verified_contact_email")
    private String verifiedContactEmail;

    @JsonProperty("pay_date")
    private String payDate;

    @JsonProperty("credit_score")
    private int creditScore;

    @JsonProperty("user_ability_extend")
    private int userAbilityExtend;

    @JsonProperty("pay_remind")
    private int payRemind;

    @JsonProperty("brand_ability")
    private int brandAbility;

    @JsonProperty("super_topic_not_syn_count")
    private int superTopicNotSynCount;

    @JsonProperty("live_ability")
    private int liveAbility;

    @JsonProperty("cardid")
    private String cardid;

    @JsonProperty("is_teenager_list")
    private int isTeenagerList;

    @JsonProperty("video_status_count")
    private int videoStatusCount;

    @JsonProperty("newbrand_ability")
    private int newbrandAbility;

    @JsonProperty("verified_level")
    private int verifiedLevel;

    @JsonProperty("urisk")
    private int urisk;

    @JsonProperty("star")
    private int star;

    @JsonProperty("status_total_counter")
    private StatusTotalCounter statusTotalCounter;

    @JsonProperty("has_service_tel")
    private boolean hasServiceTel;

    @JsonProperty("block_app")
    private int blockApp;

    @JsonProperty("planet_video")
    private int planetVideo;

    @JsonProperty("gongyi_ability")
    private int gongyiAbility;

    @JsonProperty("hardfan_ability")
    private int hardfanAbility;

    @JsonProperty("insecurity")
    private Insecurity insecurity;

    @JsonProperty("verified_source")
    private String verifiedSource;

    @JsonProperty("urank")
    private int urank;

    @JsonProperty("verified_trade")
    private String verifiedTrade;

    @JsonProperty("green_mode")
    private int greenMode;

    @JsonProperty("mb_expire_time")
    private int mbExpireTime;

    @JsonProperty("verified_source_url")
    private String verifiedSourceUrl;

    @JsonProperty("video_mark")
    private int videoMark;

    @JsonProperty("live_status")
    private int liveStatus;

    @JsonProperty("special_follow")
    private boolean specialFollow;

    @JsonProperty("followers_count_str")
    private String followersCountStr;

    @JsonProperty("chaohua_ability")
    private int chaohuaAbility;

    @JsonProperty("like")
    private boolean like;

    @JsonProperty("verified_type_ext")
    private int verifiedTypeExt;

    @JsonProperty("pagefriends_count")
    private int pagefriendsCount;

    @JsonProperty("cover_image_phone")
    private String coverImagePhone;

    @JsonProperty("ptype")
    private int ptype;

    @JsonProperty("verified_reason_url")
    private String verifiedReasonUrl;

    @JsonProperty("block_word")
    private int blockWord;

    @JsonProperty("verified_state")
    private int verifiedState;

    @JsonProperty("avatar_type")
    private int avatarType;

    @JsonProperty("hongbaofei")
    private int hongbaofei;

    @JsonProperty("video_play_count")
    private int videoPlayCount;

    @JsonProperty("mbtype")
    private int mbtype;

    @JsonProperty("user_ability")
    private int userAbility;

    @JsonProperty("story_read_state")
    private int storyReadState;

    @JsonProperty("mbrank")
    private int mbrank;

    @JsonProperty("class")
    private int jsonMemberClass;

    @JsonProperty("pc_new")
    private int pcNew;

    @JsonProperty("paycolumn_ability")
    private int paycolumnAbility;

    @JsonProperty("brand_account")
    private int brandAccount;

    @JsonProperty("verified_contact_name")
    private String verifiedContactName;

    @JsonProperty("vclub_member")
    private int vclubMember;

    @JsonProperty("is_guardian")
    private int isGuardian;

    @JsonProperty("svip")
    private int svip;

    @JsonProperty("verified_reason_modified")
    private String verifiedReasonModified;

    private Integer block;

    @JsonProperty("block_me")
    private Integer blockMe;

    @Override
    public String getName() {
        return idstr;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * 微博（status）
     *
     * @since 0.0.1
     * @see <a href=
     * "https://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84">常见返回对象数据结构</a>
     */
    public static class Status {

        /**
         *
         */
        private Integer version;

        /**
         *
         */
        private String picStatus;

        /**
         *
         */
        @JsonProperty("show_mlevel")
        private Integer showMlevel;

        /**
         * 微博创建时间
         */
        @JsonFormat(pattern = "E MMM dd HH:mm:ss '+0800' yyyy", locale = "en")
        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        /**
         * 微博ID
         */
        @JsonProperty("id")
        private long id;

        /**
         * 微博MID
         */
        @JsonProperty("mid")
        private String mid;

        /**
         * 字符串型的微博ID
         */
        @JsonProperty("idstr")
        private String idstr;

        /**
         * 微博信息内容
         */
        @JsonProperty("text")
        private String text;

        /**
         * 微博来源
         */
        @JsonProperty("source")
        private String source;

        /**
         * 是否已收藏，true：是，false：否
         */
        @JsonProperty("favorited")
        private boolean favorited;

        /**
         * 是否被截断，true：是，false：否
         */
        @JsonProperty("truncated")
        private boolean truncated;

        /**
         * （暂未支持）回复ID
         */
        @JsonProperty("in_reply_to_status_id")
        private String inReplyToStatusId;

        /**
         * （暂未支持）回复人UID
         */
        @JsonProperty("in_reply_to_user_id")
        private String inReplyToUserId;

        /**
         * （暂未支持）回复人昵称
         */
        @JsonProperty("in_reply_to_screen_name")
        private String inReplyToScreenName;

        /**
         * 缩略图片地址，没有时不返回此字段
         */
        @JsonProperty("thumbnail_pic")
        private String thumbnailPic;

        /**
         * 中等尺寸图片地址，没有时不返回此字段
         */
        @JsonProperty("bmiddle_pic")
        private String bmiddlePic;

        /**
         * 原始图片地址，没有时不返回此字段
         */
        @JsonProperty("original_pic")
        private String originalPic;

        /**
         * 地理信息字段
         * @see <a href=
         * "https://open.weibo.com/wiki/%E5%B8%B8%E8%A7%81%E8%BF%94%E5%9B%9E%E5%AF%B9%E8%B1%A1%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84#.E5.9C.B0.E7.90.86.E4.BF.A1.E6.81.AF.EF.BC.88geo.EF.BC.89">地理信息（geo）</a>
         */
        @JsonProperty("geo")
        private Object geo;

        /**
         * 转发数
         */
        @JsonProperty("reposts_count")
        private int repostsCount;

        /**
         * 评论数
         */
        @JsonProperty("comments_count")
        private int commentsCount;

        /**
         * 表态数
         */
        @JsonProperty("attitudes_count")
        private int attitudesCount;

        /**
         * 暂未支持
         */
        @JsonProperty("mlevel")
        private int mlevel;

        /**
         * 微博的可见性及指定可见分组信息。该object中type取值，0：普通微博，1：私密微博，3：指定分组微博，4：密友微博；list_id为分组的组号
         */
        @JsonProperty("visible")
        private Visible visible;

        @JsonProperty("isLongText")
        private boolean isLongText;

        @JsonProperty("hot_weibo_tags")
        private List<Object> hotWeiboTags;

        @JsonProperty("annotations")
        private List<AnnotationsItem> annotations;

        @JsonProperty("mblogtype")
        private int mblogtype;

        @JsonProperty("rid")
        private String rid;

        @JsonProperty("positive_recom_flag")
        private int positiveRecomFlag;

        @JsonProperty("can_reprint")
        private boolean canReprint;

        @JsonProperty("is_show_bulletin")
        private int isShowBulletin;

        @JsonProperty("hide_flag")
        private int hideFlag;

        @JsonProperty("hasActionTypeCard")
        private int hasActionTypeCard;

        @JsonProperty("new_comment_style")
        private int newCommentStyle;

        @JsonProperty("mblog_vip_type")
        private int mblogVipType;

        @JsonProperty("content_auth")
        private int contentAuth;

        @JsonProperty("gif_ids")
        private String gifIds;

        @JsonProperty("source_type")
        private int sourceType;

        @JsonProperty("pic_urls")
        private List<PicUrlsItem> picUrls;

        @JsonProperty("biz_feature")
        private long bizFeature;

        @JsonProperty("userType")
        private int userType;

        @JsonProperty("text_tag_tips")
        private List<Object> textTagTips;

        @JsonProperty("darwin_tags")
        private List<Object> darwinTags;

        @JsonProperty("pending_approval_count")
        private int pendingApprovalCount;

        @JsonProperty("pic_num")
        private int picNum;

        @JsonProperty("is_paid")
        private boolean isPaid;

        @JsonProperty("reward_exhibition_type")
        private int rewardExhibitionType;

        @JsonProperty("reprint_cmt_count")
        private int reprintCmtCount;

        @JsonProperty("can_edit")
        private boolean canEdit;

        @JsonProperty("textLength")
        private int textLength;

        @JsonProperty("source_allowclick")
        private int sourceAllowclick;

        @JsonProperty("show_additional_indication")
        private int showAdditionalIndication;

        @JsonProperty("comment_manage_info")
        private CommentManageInfo commentManageInfo;

        @JsonProperty("more_info_type")
        private int moreInfoType;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getPicStatus() {
            return picStatus;
        }

        public void setPicStatus(String picStatus) {
            this.picStatus = picStatus;
        }

        public Integer getShowMlevel() {
            return showMlevel;
        }

        public void setShowMlevel(Integer showMlevel) {
            this.showMlevel = showMlevel;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getIdstr() {
            return idstr;
        }

        public void setIdstr(String idstr) {
            this.idstr = idstr;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public boolean isFavorited() {
            return favorited;
        }

        public void setFavorited(boolean favorited) {
            this.favorited = favorited;
        }

        public boolean isTruncated() {
            return truncated;
        }

        public void setTruncated(boolean truncated) {
            this.truncated = truncated;
        }

        public String getInReplyToStatusId() {
            return inReplyToStatusId;
        }

        public void setInReplyToStatusId(String inReplyToStatusId) {
            this.inReplyToStatusId = inReplyToStatusId;
        }

        public String getInReplyToUserId() {
            return inReplyToUserId;
        }

        public void setInReplyToUserId(String inReplyToUserId) {
            this.inReplyToUserId = inReplyToUserId;
        }

        public String getInReplyToScreenName() {
            return inReplyToScreenName;
        }

        public void setInReplyToScreenName(String inReplyToScreenName) {
            this.inReplyToScreenName = inReplyToScreenName;
        }

        public String getThumbnailPic() {
            return thumbnailPic;
        }

        public void setThumbnailPic(String thumbnailPic) {
            this.thumbnailPic = thumbnailPic;
        }

        public String getBmiddlePic() {
            return bmiddlePic;
        }

        public void setBmiddlePic(String bmiddlePic) {
            this.bmiddlePic = bmiddlePic;
        }

        public String getOriginalPic() {
            return originalPic;
        }

        public void setOriginalPic(String originalPic) {
            this.originalPic = originalPic;
        }

        public Object getGeo() {
            return geo;
        }

        public void setGeo(Object geo) {
            this.geo = geo;
        }

        public int getRepostsCount() {
            return repostsCount;
        }

        public void setRepostsCount(int repostsCount) {
            this.repostsCount = repostsCount;
        }

        public int getCommentsCount() {
            return commentsCount;
        }

        public void setCommentsCount(int commentsCount) {
            this.commentsCount = commentsCount;
        }

        public int getAttitudesCount() {
            return attitudesCount;
        }

        public void setAttitudesCount(int attitudesCount) {
            this.attitudesCount = attitudesCount;
        }

        public int getMlevel() {
            return mlevel;
        }

        public void setMlevel(int mlevel) {
            this.mlevel = mlevel;
        }

        public Visible getVisible() {
            return visible;
        }

        public void setVisible(Visible visible) {
            this.visible = visible;
        }

        public boolean isLongText() {
            return isLongText;
        }

        public void setLongText(boolean longText) {
            isLongText = longText;
        }

        public List<Object> getHotWeiboTags() {
            return hotWeiboTags;
        }

        public void setHotWeiboTags(List<Object> hotWeiboTags) {
            this.hotWeiboTags = hotWeiboTags;
        }

        public List<AnnotationsItem> getAnnotations() {
            return annotations;
        }

        public void setAnnotations(List<AnnotationsItem> annotations) {
            this.annotations = annotations;
        }

        public int getMblogtype() {
            return mblogtype;
        }

        public void setMblogtype(int mblogtype) {
            this.mblogtype = mblogtype;
        }

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public int getPositiveRecomFlag() {
            return positiveRecomFlag;
        }

        public void setPositiveRecomFlag(int positiveRecomFlag) {
            this.positiveRecomFlag = positiveRecomFlag;
        }

        public boolean isCanReprint() {
            return canReprint;
        }

        public void setCanReprint(boolean canReprint) {
            this.canReprint = canReprint;
        }

        public int getIsShowBulletin() {
            return isShowBulletin;
        }

        public void setIsShowBulletin(int isShowBulletin) {
            this.isShowBulletin = isShowBulletin;
        }

        public int getHideFlag() {
            return hideFlag;
        }

        public void setHideFlag(int hideFlag) {
            this.hideFlag = hideFlag;
        }

        public int getHasActionTypeCard() {
            return hasActionTypeCard;
        }

        public void setHasActionTypeCard(int hasActionTypeCard) {
            this.hasActionTypeCard = hasActionTypeCard;
        }

        public int getNewCommentStyle() {
            return newCommentStyle;
        }

        public void setNewCommentStyle(int newCommentStyle) {
            this.newCommentStyle = newCommentStyle;
        }

        public int getMblogVipType() {
            return mblogVipType;
        }

        public void setMblogVipType(int mblogVipType) {
            this.mblogVipType = mblogVipType;
        }

        public int getContentAuth() {
            return contentAuth;
        }

        public void setContentAuth(int contentAuth) {
            this.contentAuth = contentAuth;
        }

        public String getGifIds() {
            return gifIds;
        }

        public void setGifIds(String gifIds) {
            this.gifIds = gifIds;
        }

        public int getSourceType() {
            return sourceType;
        }

        public void setSourceType(int sourceType) {
            this.sourceType = sourceType;
        }

        public List<PicUrlsItem> getPicUrls() {
            return picUrls;
        }

        public void setPicUrls(List<PicUrlsItem> picUrls) {
            this.picUrls = picUrls;
        }

        public long getBizFeature() {
            return bizFeature;
        }

        public void setBizFeature(long bizFeature) {
            this.bizFeature = bizFeature;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public List<Object> getTextTagTips() {
            return textTagTips;
        }

        public void setTextTagTips(List<Object> textTagTips) {
            this.textTagTips = textTagTips;
        }

        public List<Object> getDarwinTags() {
            return darwinTags;
        }

        public void setDarwinTags(List<Object> darwinTags) {
            this.darwinTags = darwinTags;
        }

        public int getPendingApprovalCount() {
            return pendingApprovalCount;
        }

        public void setPendingApprovalCount(int pendingApprovalCount) {
            this.pendingApprovalCount = pendingApprovalCount;
        }

        public int getPicNum() {
            return picNum;
        }

        public void setPicNum(int picNum) {
            this.picNum = picNum;
        }

        public boolean isPaid() {
            return isPaid;
        }

        public void setPaid(boolean paid) {
            isPaid = paid;
        }

        public int getRewardExhibitionType() {
            return rewardExhibitionType;
        }

        public void setRewardExhibitionType(int rewardExhibitionType) {
            this.rewardExhibitionType = rewardExhibitionType;
        }

        public int getReprintCmtCount() {
            return reprintCmtCount;
        }

        public void setReprintCmtCount(int reprintCmtCount) {
            this.reprintCmtCount = reprintCmtCount;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public int getTextLength() {
            return textLength;
        }

        public void setTextLength(int textLength) {
            this.textLength = textLength;
        }

        public int getSourceAllowclick() {
            return sourceAllowclick;
        }

        public void setSourceAllowclick(int sourceAllowclick) {
            this.sourceAllowclick = sourceAllowclick;
        }

        public int getShowAdditionalIndication() {
            return showAdditionalIndication;
        }

        public void setShowAdditionalIndication(int showAdditionalIndication) {
            this.showAdditionalIndication = showAdditionalIndication;
        }

        public CommentManageInfo getCommentManageInfo() {
            return commentManageInfo;
        }

        public void setCommentManageInfo(CommentManageInfo commentManageInfo) {
            this.commentManageInfo = commentManageInfo;
        }

        public int getMoreInfoType() {
            return moreInfoType;
        }

        public void setMoreInfoType(int moreInfoType) {
            this.moreInfoType = moreInfoType;
        }
    }

    public static class StatusTotalCounter {

        @JsonProperty("total_cnt")
        private int totalCnt;

        @JsonProperty("repost_cnt")
        private int repostCnt;

        @JsonProperty("comment_like_cnt")
        private int commentLikeCnt;

        @JsonProperty("like_cnt")
        private int likeCnt;

        @JsonProperty("comment_cnt")
        private int commentCnt;

        public int getTotalCnt() {
            return totalCnt;
        }

        public void setTotalCnt(int totalCnt) {
            this.totalCnt = totalCnt;
        }

        public int getRepostCnt() {
            return repostCnt;
        }

        public void setRepostCnt(int repostCnt) {
            this.repostCnt = repostCnt;
        }

        public int getCommentLikeCnt() {
            return commentLikeCnt;
        }

        public void setCommentLikeCnt(int commentLikeCnt) {
            this.commentLikeCnt = commentLikeCnt;
        }

        public int getLikeCnt() {
            return likeCnt;
        }

        public void setLikeCnt(int likeCnt) {
            this.likeCnt = likeCnt;
        }

        public int getCommentCnt() {
            return commentCnt;
        }

        public void setCommentCnt(int commentCnt) {
            this.commentCnt = commentCnt;
        }
    }

    public static class VideoTotalCounter {

        @JsonProperty("play_cnt")
        private int playCnt;

        public int getPlayCnt() {
            return playCnt;
        }

        public void setPlayCnt(int playCnt) {
            this.playCnt = playCnt;
        }
    }

    public static class Visible {

        @JsonProperty("list_id")
        private int listId;

        @JsonProperty("type")
        private int type;

        public int getListId() {
            return listId;
        }

        public void setListId(int listId) {
            this.listId = listId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    public static class AnnotationsItem {

        @JsonProperty("mapi_request")
        private boolean mapiRequest;

        @JsonProperty("client_mblogid")
        private String clientMblogid;

        @JsonProperty("photo_sub_type")
        private String photoSubType;

        public boolean isMapiRequest() {
            return mapiRequest;
        }

        public void setMapiRequest(boolean mapiRequest) {
            this.mapiRequest = mapiRequest;
        }

        public String getClientMblogid() {
            return clientMblogid;
        }

        public void setClientMblogid(String clientMblogid) {
            this.clientMblogid = clientMblogid;
        }

        public String getPhotoSubType() {
            return photoSubType;
        }

        public void setPhotoSubType(String photoSubType) {
            this.photoSubType = photoSubType;
        }
    }

    public static class CommentManageInfo {

        @JsonProperty("comment_permission_type")
        private int commentPermissionType;

        @JsonProperty("comment_sort_type")
        private int commentSortType;

        @JsonProperty("approval_comment_type")
        private int approvalCommentType;

        public int getCommentPermissionType() {
            return commentPermissionType;
        }

        public void setCommentPermissionType(int commentPermissionType) {
            this.commentPermissionType = commentPermissionType;
        }

        public int getCommentSortType() {
            return commentSortType;
        }

        public void setCommentSortType(int commentSortType) {
            this.commentSortType = commentSortType;
        }

        public int getApprovalCommentType() {
            return approvalCommentType;
        }

        public void setApprovalCommentType(int approvalCommentType) {
            this.approvalCommentType = approvalCommentType;
        }
    }

    public static class PicUrlsItem {
        @JsonProperty("thumbnail_pic")
        private String thumbnailPic;

        public String getThumbnailPic() {
            return thumbnailPic;
        }

        public void setThumbnailPic(String thumbnailPic) {
            this.thumbnailPic = thumbnailPic;
        }
    }

    public static class Insecurity {

        @JsonProperty("sexual_content")
        private boolean sexualContent;

        public boolean isSexualContent() {
            return sexualContent;
        }

        public void setSexualContent(boolean sexualContent) {
            this.sexualContent = sexualContent;
        }
    }

    public void setAuthorities(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getNameAttributeKey() {
        return nameAttributeKey;
    }

    public void setNameAttributeKey(String nameAttributeKey) {
        this.nameAttributeKey = nameAttributeKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isAllowAllActMsg() {
        return allowAllActMsg;
    }

    public void setAllowAllActMsg(boolean allowAllActMsg) {
        this.allowAllActMsg = allowAllActMsg;
    }

    public boolean isGeoEnabled() {
        return geoEnabled;
    }

    public void setGeoEnabled(boolean geoEnabled) {
        this.geoEnabled = geoEnabled;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getVerifiedType() {
        return verifiedType;
    }

    public void setVerifiedType(int verifiedType) {
        this.verifiedType = verifiedType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isAllowAllComment() {
        return allowAllComment;
    }

    public void setAllowAllComment(boolean allowAllComment) {
        this.allowAllComment = allowAllComment;
    }

    public String getAvatarLarge() {
        return avatarLarge;
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getAvatarHd() {
        return avatarHd;
    }

    public void setAvatarHd(String avatarHd) {
        this.avatarHd = avatarHd;
    }

    public String getVerifiedReason() {
        return verifiedReason;
    }

    public void setVerifiedReason(String verifiedReason) {
        this.verifiedReason = verifiedReason;
    }

    public boolean isFollowMe() {
        return followMe;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe = followMe;
    }

    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public int getBiFollowersCount() {
        return biFollowersCount;
    }

    public void setBiFollowersCount(int biFollowersCount) {
        this.biFollowersCount = biFollowersCount;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getIsTeenager() {
        return isTeenager;
    }

    public void setIsTeenager(int isTeenager) {
        this.isTeenager = isTeenager;
    }

    public int getVplusAbility() {
        return vplusAbility;
    }

    public void setVplusAbility(int vplusAbility) {
        this.vplusAbility = vplusAbility;
    }

    public boolean isLikeMe() {
        return likeMe;
    }

    public void setLikeMe(boolean likeMe) {
        this.likeMe = likeMe;
    }

    public String getVerifiedContactMobile() {
        return verifiedContactMobile;
    }

    public void setVerifiedContactMobile(String verifiedContactMobile) {
        this.verifiedContactMobile = verifiedContactMobile;
    }

    public boolean isLightRing() {
        return lightRing;
    }

    public void setLightRing(boolean lightRing) {
        this.lightRing = lightRing;
    }

    public int getWendaAbility() {
        return wendaAbility;
    }

    public void setWendaAbility(int wendaAbility) {
        this.wendaAbility = wendaAbility;
    }

    public VideoTotalCounter getVideoTotalCounter() {
        return videoTotalCounter;
    }

    public void setVideoTotalCounter(VideoTotalCounter videoTotalCounter) {
        this.videoTotalCounter = videoTotalCounter;
    }

    public int getNftAbility() {
        return nftAbility;
    }

    public void setNftAbility(int nftAbility) {
        this.nftAbility = nftAbility;
    }

    public int getEcommerceAbility() {
        return ecommerceAbility;
    }

    public void setEcommerceAbility(int ecommerceAbility) {
        this.ecommerceAbility = ecommerceAbility;
    }

    public String getVerifiedContactEmail() {
        return verifiedContactEmail;
    }

    public void setVerifiedContactEmail(String verifiedContactEmail) {
        this.verifiedContactEmail = verifiedContactEmail;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getUserAbilityExtend() {
        return userAbilityExtend;
    }

    public void setUserAbilityExtend(int userAbilityExtend) {
        this.userAbilityExtend = userAbilityExtend;
    }

    public int getPayRemind() {
        return payRemind;
    }

    public void setPayRemind(int payRemind) {
        this.payRemind = payRemind;
    }

    public int getBrandAbility() {
        return brandAbility;
    }

    public void setBrandAbility(int brandAbility) {
        this.brandAbility = brandAbility;
    }

    public int getSuperTopicNotSynCount() {
        return superTopicNotSynCount;
    }

    public void setSuperTopicNotSynCount(int superTopicNotSynCount) {
        this.superTopicNotSynCount = superTopicNotSynCount;
    }

    public int getLiveAbility() {
        return liveAbility;
    }

    public void setLiveAbility(int liveAbility) {
        this.liveAbility = liveAbility;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public int getIsTeenagerList() {
        return isTeenagerList;
    }

    public void setIsTeenagerList(int isTeenagerList) {
        this.isTeenagerList = isTeenagerList;
    }

    public int getVideoStatusCount() {
        return videoStatusCount;
    }

    public void setVideoStatusCount(int videoStatusCount) {
        this.videoStatusCount = videoStatusCount;
    }

    public int getNewbrandAbility() {
        return newbrandAbility;
    }

    public void setNewbrandAbility(int newbrandAbility) {
        this.newbrandAbility = newbrandAbility;
    }

    public int getVerifiedLevel() {
        return verifiedLevel;
    }

    public void setVerifiedLevel(int verifiedLevel) {
        this.verifiedLevel = verifiedLevel;
    }

    public int getUrisk() {
        return urisk;
    }

    public void setUrisk(int urisk) {
        this.urisk = urisk;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public StatusTotalCounter getStatusTotalCounter() {
        return statusTotalCounter;
    }

    public void setStatusTotalCounter(StatusTotalCounter statusTotalCounter) {
        this.statusTotalCounter = statusTotalCounter;
    }

    public boolean isHasServiceTel() {
        return hasServiceTel;
    }

    public void setHasServiceTel(boolean hasServiceTel) {
        this.hasServiceTel = hasServiceTel;
    }

    public int getBlockApp() {
        return blockApp;
    }

    public void setBlockApp(int blockApp) {
        this.blockApp = blockApp;
    }

    public int getPlanetVideo() {
        return planetVideo;
    }

    public void setPlanetVideo(int planetVideo) {
        this.planetVideo = planetVideo;
    }

    public int getGongyiAbility() {
        return gongyiAbility;
    }

    public void setGongyiAbility(int gongyiAbility) {
        this.gongyiAbility = gongyiAbility;
    }

    public int getHardfanAbility() {
        return hardfanAbility;
    }

    public void setHardfanAbility(int hardfanAbility) {
        this.hardfanAbility = hardfanAbility;
    }

    public Insecurity getInsecurity() {
        return insecurity;
    }

    public void setInsecurity(Insecurity insecurity) {
        this.insecurity = insecurity;
    }

    public String getVerifiedSource() {
        return verifiedSource;
    }

    public void setVerifiedSource(String verifiedSource) {
        this.verifiedSource = verifiedSource;
    }

    public int getUrank() {
        return urank;
    }

    public void setUrank(int urank) {
        this.urank = urank;
    }

    public String getVerifiedTrade() {
        return verifiedTrade;
    }

    public void setVerifiedTrade(String verifiedTrade) {
        this.verifiedTrade = verifiedTrade;
    }

    public int getGreenMode() {
        return greenMode;
    }

    public void setGreenMode(int greenMode) {
        this.greenMode = greenMode;
    }

    public int getMbExpireTime() {
        return mbExpireTime;
    }

    public void setMbExpireTime(int mbExpireTime) {
        this.mbExpireTime = mbExpireTime;
    }

    public String getVerifiedSourceUrl() {
        return verifiedSourceUrl;
    }

    public void setVerifiedSourceUrl(String verifiedSourceUrl) {
        this.verifiedSourceUrl = verifiedSourceUrl;
    }

    public int getVideoMark() {
        return videoMark;
    }

    public void setVideoMark(int videoMark) {
        this.videoMark = videoMark;
    }

    public int getLiveStatus() {
        return liveStatus;
    }

    public void setLiveStatus(int liveStatus) {
        this.liveStatus = liveStatus;
    }

    public boolean isSpecialFollow() {
        return specialFollow;
    }

    public void setSpecialFollow(boolean specialFollow) {
        this.specialFollow = specialFollow;
    }

    public String getFollowersCountStr() {
        return followersCountStr;
    }

    public void setFollowersCountStr(String followersCountStr) {
        this.followersCountStr = followersCountStr;
    }

    public int getChaohuaAbility() {
        return chaohuaAbility;
    }

    public void setChaohuaAbility(int chaohuaAbility) {
        this.chaohuaAbility = chaohuaAbility;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public int getVerifiedTypeExt() {
        return verifiedTypeExt;
    }

    public void setVerifiedTypeExt(int verifiedTypeExt) {
        this.verifiedTypeExt = verifiedTypeExt;
    }

    public int getPagefriendsCount() {
        return pagefriendsCount;
    }

    public void setPagefriendsCount(int pagefriendsCount) {
        this.pagefriendsCount = pagefriendsCount;
    }

    public String getCoverImagePhone() {
        return coverImagePhone;
    }

    public void setCoverImagePhone(String coverImagePhone) {
        this.coverImagePhone = coverImagePhone;
    }

    public int getPtype() {
        return ptype;
    }

    public void setPtype(int ptype) {
        this.ptype = ptype;
    }

    public String getVerifiedReasonUrl() {
        return verifiedReasonUrl;
    }

    public void setVerifiedReasonUrl(String verifiedReasonUrl) {
        this.verifiedReasonUrl = verifiedReasonUrl;
    }

    public int getBlockWord() {
        return blockWord;
    }

    public void setBlockWord(int blockWord) {
        this.blockWord = blockWord;
    }

    public int getVerifiedState() {
        return verifiedState;
    }

    public void setVerifiedState(int verifiedState) {
        this.verifiedState = verifiedState;
    }

    public int getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public int getHongbaofei() {
        return hongbaofei;
    }

    public void setHongbaofei(int hongbaofei) {
        this.hongbaofei = hongbaofei;
    }

    public int getVideoPlayCount() {
        return videoPlayCount;
    }

    public void setVideoPlayCount(int videoPlayCount) {
        this.videoPlayCount = videoPlayCount;
    }

    public int getMbtype() {
        return mbtype;
    }

    public void setMbtype(int mbtype) {
        this.mbtype = mbtype;
    }

    public int getUserAbility() {
        return userAbility;
    }

    public void setUserAbility(int userAbility) {
        this.userAbility = userAbility;
    }

    public int getStoryReadState() {
        return storyReadState;
    }

    public void setStoryReadState(int storyReadState) {
        this.storyReadState = storyReadState;
    }

    public int getMbrank() {
        return mbrank;
    }

    public void setMbrank(int mbrank) {
        this.mbrank = mbrank;
    }

    public int getJsonMemberClass() {
        return jsonMemberClass;
    }

    public void setJsonMemberClass(int jsonMemberClass) {
        this.jsonMemberClass = jsonMemberClass;
    }

    public int getPcNew() {
        return pcNew;
    }

    public void setPcNew(int pcNew) {
        this.pcNew = pcNew;
    }

    public int getPaycolumnAbility() {
        return paycolumnAbility;
    }

    public void setPaycolumnAbility(int paycolumnAbility) {
        this.paycolumnAbility = paycolumnAbility;
    }

    public int getBrandAccount() {
        return brandAccount;
    }

    public void setBrandAccount(int brandAccount) {
        this.brandAccount = brandAccount;
    }

    public String getVerifiedContactName() {
        return verifiedContactName;
    }

    public void setVerifiedContactName(String verifiedContactName) {
        this.verifiedContactName = verifiedContactName;
    }

    public int getVclubMember() {
        return vclubMember;
    }

    public void setVclubMember(int vclubMember) {
        this.vclubMember = vclubMember;
    }

    public int getIsGuardian() {
        return isGuardian;
    }

    public void setIsGuardian(int isGuardian) {
        this.isGuardian = isGuardian;
    }

    public int getSvip() {
        return svip;
    }

    public void setSvip(int svip) {
        this.svip = svip;
    }

    public String getVerifiedReasonModified() {
        return verifiedReasonModified;
    }

    public void setVerifiedReasonModified(String verifiedReasonModified) {
        this.verifiedReasonModified = verifiedReasonModified;
    }

    public Integer getBlock() {
        return block;
    }

    public void setBlock(Integer block) {
        this.block = block;
    }

    public Integer getBlockMe() {
        return blockMe;
    }

    public void setBlockMe(Integer blockMe) {
        this.blockMe = blockMe;
    }
}
