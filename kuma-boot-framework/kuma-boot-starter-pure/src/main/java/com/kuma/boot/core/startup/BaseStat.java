package com.kuma.boot.core.startup;

import java.util.HashMap;
import java.util.Map;

public class BaseStat {

    private final Map<String, String> attributes = new HashMap<>();

    private String name;

    private long startTime;

    private long endTime;

    private long cost;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }

    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
        this.cost = this.endTime - this.startTime;
    }

    public long getCost() { return cost; }
    public void setCost(long cost) { this.cost = cost; }

    public Map<String, String> getAttributes() { return attributes; }

    public void putAttribute(String key, String value) { this.attributes.put(key, value); }

    public String getAttribute(String key) { return this.attributes.get(key); }
}
