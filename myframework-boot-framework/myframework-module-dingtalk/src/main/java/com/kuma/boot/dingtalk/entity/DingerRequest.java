/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.entity;

import java.util.ArrayList;
import java.util.List;

public class DingerRequest {
    private String content;
    private String title;
    private List<String> phones = new ArrayList<String>();
    private boolean atAll = false;

    private DingerRequest(String content) {
        this.content = content;
    }

    private DingerRequest(String content, String title) {
        this(content);
        this.title = title;
    }

    private DingerRequest(String content, List<String> phones) {
        this.content = content;
        this.phones = phones;
    }

    private DingerRequest(String content, boolean atAll) {
        this.content = content;
        this.atAll = atAll;
    }

    private DingerRequest(String content, String title, List<String> phones) {
        this(content, title);
        this.phones = phones;
    }

    private DingerRequest(String content, String title, boolean atAll) {
        this(content, atAll);
        this.title = title;
    }

    public static DingerRequest request(String content) {
        return new DingerRequest(content);
    }

    public static DingerRequest request(String content, String title) {
        return new DingerRequest(content, title);
    }

    public static DingerRequest request(String content, List<String> phones) {
        return new DingerRequest(content, phones);
    }

    public static DingerRequest request(String content, boolean atAll) {
        return new DingerRequest(content, atAll);
    }

    public static DingerRequest request(String content, String title, List<String> phones) {
        return new DingerRequest(content, title, phones);
    }

    public static DingerRequest request(String content, String title, boolean atAll) {
        return new DingerRequest(content, title, atAll);
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPhones() {
        return this.phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public boolean isAtAll() {
        return this.atAll;
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }
}

