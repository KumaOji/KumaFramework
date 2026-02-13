/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.io;

import com.kuma.boot.common.exception.BootException;
import com.kuma.boot.common.support.handler.MapHandler;
import com.kuma.boot.common.utils.common.RandomUtils;
import com.kuma.boot.common.utils.io.FileUtils;
import com.kuma.boot.common.utils.lang.ObjectUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class FileStreamUtils {
    private FileStreamUtils() {
    }

    @Deprecated
    public static String toString(InputStream is, String charset) {
        String string;
        ByteArrayOutputStream boa = new ByteArrayOutputStream();
        try {
            int len;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                boa.write(buffer, 0, len);
            }
            string = boa.toString(charset);
        }
        catch (Throwable throwable) {
            try {
                try {
                    boa.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                throw new BootException(e);
            }
        }
        boa.close();
        return string;
    }

    @Deprecated
    public static String toString(InputStream is) {
        return FileStreamUtils.toString(is, "UTF-8");
    }

    public static String getFileContent(String path) {
        return FileStreamUtils.getFileContent(path, "UTF-8");
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static String getFileContent(String path, String charset) {
        try (InputStream inputStream = FileStreamUtils.getInputStream(path);){
            String string;
            try (ByteArrayOutputStream boa = new ByteArrayOutputStream();){
                int len;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    boa.write(buffer, 0, len);
                }
                string = boa.toString(charset);
            }
            return string;
        }
        catch (Exception e) {
            throw new BootException(e);
        }
    }

    public static byte[] getFileBytes(String filePath) {
        InputStream inputStream = FileStreamUtils.getInputStream(filePath);
        return FileStreamUtils.inputStreamToBytes(inputStream);
    }

    public static InputStream getInputStream(String filePath) {
        InputStream inputStream;
        try {
            inputStream = URL.of(URI.create(filePath), null).openStream();
        }
        catch (MalformedURLException localMalformedURLException) {
            try {
                inputStream = new FileInputStream(filePath);
            }
            catch (Exception localException2) {
                ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
                if (localClassLoader == null) {
                    localClassLoader = FileStreamUtils.class.getClassLoader();
                }
                if ((inputStream = localClassLoader.getResourceAsStream(filePath)) == null) {
                    throw new BootException("Could not find file: " + filePath);
                }
            }
        }
        catch (IOException localIOException1) {
            throw new BootException(localIOException1);
        }
        return inputStream;
    }

    public static void closeStream(Closeable closeable) {
        if (ObjectUtils.isNotNull(closeable)) {
            try {
                closeable.close();
            }
            catch (IOException e) {
                throw new BootException(e);
            }
        }
    }

    public static List<String> readAllLines(String path) {
        InputStream inputStream = FileStreamUtils.class.getResourceAsStream(path);
        return FileStreamUtils.readAllLines(inputStream, "UTF-8", true);
    }

    public static List<String> readAllLines(InputStream is) {
        return FileStreamUtils.readAllLines(is, "UTF-8", true);
    }

    public static List<String> readAllLines(InputStream is, String charset, boolean ignoreEmpty) {
        try {
            ArrayList<String> lines = new ArrayList<String>();
            BufferedReader e = new BufferedReader(new InputStreamReader(is, Charset.forName(charset)));
            while (e.ready()) {
                String entry = e.readLine();
                if (StringUtils.isEmpty(entry) && ignoreEmpty) continue;
                lines.add(entry);
            }
            return lines;
        }
        catch (IOException e) {
            throw new BootException(e);
        }
    }

    public static String getFileContent(String path, int startIndex, int endIndex) {
        return FileStreamUtils.getFileContent(path, startIndex, endIndex, StandardCharsets.UTF_8);
    }

    public static String getFileContent(String path, int startIndex, int endIndex, Charset charset) {
        String string;
        block9: {
            InputStream inputStream = FileStreamUtils.class.getResourceAsStream(path);
            try {
                assert (inputStream != null);
                string = FileUtils.getFileContent(inputStream, startIndex, endIndex, charset);
                if (inputStream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw new BootException(e);
                }
            }
            inputStream.close();
        }
        return string;
    }

    public static File inputStreamToFile(InputStream inputStream, boolean deleteOnExit) {
        if (ObjectUtils.isNull(inputStream)) {
            return null;
        }
        try {
            File temp = File.createTempFile(RandomUtils.randomNumber(10), "temp");
            if (deleteOnExit) {
                temp.deleteOnExit();
            }
            Files.copy(inputStream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            File file = temp;
            return file;
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        finally {
            FileStreamUtils.closeStream(inputStream);
        }
    }

    public static File inputStreamToFile(InputStream inputStream) {
        return FileStreamUtils.inputStreamToFile(inputStream, false);
    }

    public static byte[] inputStreamToBytes(InputStream inputStream) {
        byte[] byArray;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            byArray = output.toByteArray();
        }
        catch (Throwable throwable) {
            try {
                try {
                    output.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new BootException(e);
            }
        }
        output.close();
        return byArray;
    }

    public static String inputStreamToString(InputStream inputStream, String charsetStr) {
        byte[] bytes = FileStreamUtils.inputStreamToBytes(inputStream);
        Charset charset = Charset.forName(charsetStr);
        return new String(bytes, charset);
    }

    public static String inputStreamToString(InputStream inputStream) {
        return FileStreamUtils.inputStreamToString(inputStream, "UTF-8");
    }

    public static <K, V> Map<K, V> readToMap(String path, String charset, MapHandler<K, V, String> mapHandler) {
        InputStream inputStream = FileStreamUtils.class.getResourceAsStream(path);
        return FileUtils.readToMap(inputStream, charset, mapHandler);
    }

    public static <K, V> Map<K, V> readToMap(String path, MapHandler<K, V, String> mapHandler) {
        return FileStreamUtils.readToMap(path, "UTF-8", mapHandler);
    }

    public static Map<String, String> readToMap(String path, final String splliter) {
        return FileStreamUtils.readToMap(path, new MapHandler<String, String, String>(){

            @Override
            public String getKey(String o) {
                return o.split(splliter)[0];
            }

            @Override
            public String getValue(String o) {
                return o.split(splliter)[1];
            }
        });
    }

    public static void write(Collection<String> lines, OutputStream output, Charset charset) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, charset));){
            for (String service : lines) {
                writer.write(service);
                writer.newLine();
            }
            writer.flush();
        }
        catch (IOException e) {
            throw new BootException(e);
        }
        finally {
            try {
                output.close();
            }
            catch (IOException e) {
                LogUtils.error(e);
            }
        }
    }

    public static void write(Collection<String> lines, OutputStream output) {
        FileStreamUtils.write(lines, output, StandardCharsets.UTF_8);
    }
}

