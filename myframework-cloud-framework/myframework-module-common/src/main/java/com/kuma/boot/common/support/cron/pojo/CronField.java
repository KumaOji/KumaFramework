/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.support.cron.pojo;

import com.kuma.boot.common.support.cron.pojo.CronPosition;
import com.kuma.boot.common.support.cron.util.CompareUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CronField {
    public static final String STAR = "*";
    public static final String COMMA = ",";
    public static final String HYPHEN = "-";
    public static final String SLASH = "/";
    private CronPosition cronPosition;
    private String express;
    private List<Integer> listCache = null;

    public CronField(CronPosition cronPosition, String express) {
        this.cronPosition = cronPosition;
        this.express = express;
    }

    public CronPosition getCronPosition() {
        return this.cronPosition;
    }

    public String getExpress() {
        return this.express;
    }

    public boolean containsAll() {
        return STAR.equals(this.express);
    }

    public boolean containsComma() {
        return this.express.contains(COMMA);
    }

    public boolean containsHyphen() {
        return this.express.contains(HYPHEN);
    }

    public boolean containsSlash() {
        return this.express.contains(SLASH);
    }

    public List<Integer> points() {
        int right;
        if (null != this.listCache) {
            return this.listCache;
        }
        this.listCache = new ArrayList<Integer>(5);
        int min = this.cronPosition.getMin();
        int max = this.cronPosition.getMax();
        if (STAR.equals(this.express)) {
            for (int i = min; i <= max; ++i) {
                this.listCache.add(i);
            }
            return this.listCache;
        }
        if (this.containsComma()) {
            String[] split;
            for (String part : split = this.express.split(COMMA)) {
                this.listCache.addAll(new CronField(this.cronPosition, part).points());
            }
            if (this.listCache.size() > 1) {
                CompareUtil.removeDuplicate(this.listCache);
                Collections.sort(this.listCache);
            }
            return this.listCache;
        }
        int step = 1;
        if (this.containsHyphen()) {
            strings = this.express.split(HYPHEN);
            left = Integer.parseInt(strings[0]);
            CompareUtil.assertRange(this.cronPosition, left);
            if (strings[1].contains(SLASH)) {
                String[] split = strings[1].split(SLASH);
                right = Integer.parseInt(split[0]);
                CompareUtil.assertSize(left, right);
                CompareUtil.assertRange(this.cronPosition, right);
                step = Integer.parseInt(split[1]);
            } else {
                right = Integer.parseInt(strings[1]);
                CompareUtil.assertSize(left, right);
                CompareUtil.assertRange(this.cronPosition, right);
            }
        } else if (this.containsSlash()) {
            strings = this.express.split(SLASH);
            left = Integer.parseInt(strings[0]);
            CompareUtil.assertRange(this.cronPosition, left);
            step = Integer.parseInt(strings[1]);
            right = max;
            CompareUtil.assertSize(left, right);
        } else {
            int single = Integer.parseInt(this.express);
            if (CronPosition.WEEK == this.cronPosition && 7 == single) {
                single = 0;
            }
            CompareUtil.assertRange(this.cronPosition, single);
            this.listCache.add(single);
            return this.listCache;
        }
        for (int i = left; i <= right; i += step) {
            this.listCache.add(i);
        }
        return this.listCache;
    }

    public String toString() {
        return "CronField{cronPosition=" + String.valueOf((Object)this.cronPosition) + ", express='" + this.express + "'}";
    }
}

