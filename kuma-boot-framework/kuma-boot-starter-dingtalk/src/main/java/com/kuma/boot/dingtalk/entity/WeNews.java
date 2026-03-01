/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import com.kuma.boot.dingtalk.enums.WeTalkMsgType;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WeNews
extends WeTalkMessage {
    private static final int ARTICLE_LIMIT = 8;
    private News news;

    public WeNews() {
        this.setMsgtype(WeTalkMsgType.NEWS.type());
    }

    public WeNews(List<News.Article> articles) {
        this();
        this.news = new News(articles);
    }

    public News getNews() {
        return this.news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    @Override
    public void transfer(Map<String, Object> params) {
        Map.Entry<String, Object> entry;
        Object value;
        Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator();
        if (iterator.hasNext() && (value = (entry = iterator.next()).getValue()) instanceof List) {
            List<?> imageTexts = (List<?>) value;
            int size = Math.min(imageTexts.size(), 8);
            for (int i = 0; i < size; i++) {
                ImageTextDeo imageText = (ImageTextDeo) imageTexts.get(i);
                this.news.articles.add(new News.Article(imageText.getTitle(), imageText.getDescription(), imageText.getUrl(), imageText.getPicUrl()));
            }
        }
    }

    public static class News
    implements Serializable {
        private List<Article> articles;

        public News() {
        }

        public News(List<Article> articles) {
            this.articles = articles;
        }

        public List<Article> getArticles() {
            return this.articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }

        public static class Article
        implements Serializable {
            private String title;
            private String description;
            private String url;
            private String picurl;

            public Article() {
            }

            public Article(String title, String description, String url, String picurl) {
                this.title = title;
                this.description = description;
                this.url = url;
                this.picurl = picurl;
            }

            public String getTitle() {
                return this.title;
            }

            public void setTitle(String title) {
                this.title = title;
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

            public String getPicurl() {
                return this.picurl;
            }

            public void setPicurl(String picurl) {
                this.picurl = picurl;
            }
        }
    }
}

