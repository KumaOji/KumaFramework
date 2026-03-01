package com.kuma.boot.core.holder;

import com.kuma.boot.common.constant.SystemConstants;
import com.kuma.boot.common.utils.system.SystemUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import static com.kuma.boot.common.constant.SystemConstants.KMC_RUNNING_IN_TEST;


/**
 * RuntimeContextHolder
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class RuntimeContextHolder {

    public static final String SPRING_CLOUD_CLASS_NAME = "org.springframework.cloud.bootstrap.BootstrapImportSelectorConfiguration";
    private SpringApplication springApplication;
    private String[] args;
    private Environment environment;
    private ApplicationContext applicationContext;
    private boolean isSpringCloud;
    private boolean isWindows;
    private boolean isDocker;
    private boolean isK8s;
    private boolean isLinux;
    private Set<String> basePackages;
    private volatile static RuntimeContextHolder INSTANCE;

    private RuntimeContextHolder() {
    }

    public static RuntimeContextHolder getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("请先调用fromSpringApplication方法");
        }
        return INSTANCE;
    }


    public static RuntimeContextHolder fromSpringApplication( SpringApplication springApplication,
                                                              String[] args ) {
        if (null == INSTANCE) {
            synchronized (RuntimeContextHolder.class) {
                if (null == INSTANCE) {
                    INSTANCE = new RuntimeContextHolder();
                }
            }
        }

        INSTANCE.springApplication = springApplication;
        INSTANCE.args = args;
        INSTANCE.judgmentSpringCloud(springApplication);
        INSTANCE.judgmentRuntimeEnvironment();
        return INSTANCE;
    }

    private void judgmentSpringCloud( SpringApplication springApplication ) {
        Set<Object> allSources = springApplication.getAllSources();
        for (Object source : allSources) {
            String name = ( (Class<?>) source ).getName();
            if (SPRING_CLOUD_CLASS_NAME.equals(name)) {
                isSpringCloud = true;
                return;
            }
        }
    }

    private void judgmentRuntimeEnvironment() {
        if (!SystemUtils.isLinux()) {
            isWindows = true;
            return;
        }
        isLinux = true;

        String path = "/proc/1/cgroup";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {
            String tmp;
            while (( tmp = bufferedReader.readLine() ) != null) {
                if (tmp.contains("docker")) {
                    isDocker = true;
                    return;
                }

                if (tmp.contains("kubepods")) {
                    isK8s = true;
                    return;
                }
            }
        } catch (IOException e) {

        }

    }

    public boolean isTestEnvironment() {
        String propOrEnv = SystemUtils.getPropOrEnv(KMC_RUNNING_IN_TEST);
        if ("true".equals(propOrEnv)) {
            return true;
        }

        //todo
        return false;
    }

    public SpringApplication getSpringApplication() {
        return springApplication;
    }

    public void setSpringApplication( SpringApplication springApplication ) {
        this.springApplication = springApplication;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs( String[] args ) {
        this.args = args;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment( Environment environment ) {
        this.environment = environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext( ApplicationContext applicationContext ) {
        this.applicationContext = applicationContext;
    }

    public boolean isSpringCloud() {
        return isSpringCloud;
    }

    public void setSpringCloud( boolean springCloud ) {
        isSpringCloud = springCloud;
    }

    public boolean isWindows() {
        return isWindows;
    }

    public void setWindows( boolean windows ) {
        isWindows = windows;
    }

    public boolean isDocker() {
        return isDocker;
    }

    public void setDocker( boolean docker ) {
        isDocker = docker;
    }

    public boolean isK8s() {
        return isK8s;
    }

    public void setK8s( boolean k8s ) {
        isK8s = k8s;
    }

    public boolean isLinux() {
        return isLinux;
    }

    public void setLinux( boolean linux ) {
        isLinux = linux;
    }

    public Set<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages( Set<String> basePackages ) {
        this.basePackages = basePackages;
    }

}
