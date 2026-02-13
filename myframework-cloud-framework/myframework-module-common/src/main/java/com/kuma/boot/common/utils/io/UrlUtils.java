/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.web.util.UriUtils
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.utils.common.ArgUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.util.UriUtils;

public class UrlUtils
extends UriUtils {
    public static String encode(String source) {
        return UrlUtils.encode((String)source, (Charset)StandardCharsets.UTF_8);
    }

    public static String decode(String source) {
        return UrlUtils.decode((String)source, (Charset)StandardCharsets.UTF_8);
    }

    public static List<String> readAllLines(URL url) {
        return UrlUtils.readAllLines(url, "UTF-8");
    }

    public static List<String> readAllLines(URL url, String charset) {
        ArgUtils.notNull(url, "url");
        ArgUtils.notEmpty(charset, "charset");
        ArrayList<String> resultList = new ArrayList<String>();
        try (InputStream is = url.openStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName(charset)));){
            String line;
            while ((line = br.readLine()) != null) {
                resultList.add(line);
            }
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        return resultList;
    }
}

