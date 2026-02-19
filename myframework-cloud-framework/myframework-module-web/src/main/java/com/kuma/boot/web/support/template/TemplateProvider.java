/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  com.kuma.boot.common.utils.servlet.RequestUtils
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  jakarta.servlet.http.HttpSession
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.web.support.template;

import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class TemplateProvider {
    public HttpServletRequest request() {
        return RequestUtils.getRequest();
    }

    public HttpServletResponse response() {
        return RequestUtils.getResponse();
    }

    public HttpSession session() {
        return Objects.requireNonNull(RequestUtils.getRequest()).getSession();
    }

    public void setattr(String key, Object value) {
        this.request().setAttribute(key, value);
    }

    public Object getattr(String key) {
        if (key.contains(".")) {
            Object r = null;
            String[] path = org.springframework.util.StringUtils.split((String)key, (String)".");
            if (path != null && path.length > 0) {
                for (int i = 0; i < path.length; ++i) {
                    String p = path[i];
                    if (i == 0) {
                        r = this.request().getAttribute(p);
                    } else {
                        try {
                            Field f = r.getClass().getDeclaredField(p);
                            f.setAccessible(true);
                            r = f.get(r);
                        }
                        catch (Exception exp) {
                            r = null;
                        }
                    }
                    if (r == null) break;
                }
            }
            return r;
        }
        return this.request().getAttribute(key);
    }

    public Object where(Boolean istrue, Object trueObj, Object falseObj) {
        return istrue != false ? trueObj : falseObj;
    }

    public String cutString(String str, int maxlen) {
        if (StringUtils.isEmpty((String)str)) {
            return str;
        }
        if (str.length() <= maxlen) {
            return str;
        }
        return StringUtils.subString3((String)str, (int)maxlen);
    }

    public String dateString(Date date, String format) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(format).format(date);
    }

    public String print(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Date) {
            return this.dateString((Date)o, "yyyy-MM-dd HH:mm:ss");
        }
        return o.toString();
    }
}

