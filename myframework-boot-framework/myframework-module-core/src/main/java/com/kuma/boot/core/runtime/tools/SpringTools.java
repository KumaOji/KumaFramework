package com.kuma.boot.core.runtime.tools;

import com.kuma.boot.core.runtime.tools.SpringTools.CLAZZ.DAO;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.boot.system.JavaVersion;
import org.springframework.boot.system.SystemProperties;
import org.springframework.boot.util.Instantiator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * SpringTools
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class SpringTools {

    //获取进程ID 1
    //如果你想在程序中获取当前SpringBoot运行的进程号，那么你可以使用ApplicationPid，该类非常方便的获取当前进程ID。
    public void test() {
        ApplicationPid pid = new ApplicationPid();
        System.out.printf("进程ID: %s%n", pid.toString());
    }

    //获取进程ID 2
    public void test1() {
        //#在META-INF/spring.factories中注册监听器
        //org.springframework.context.ApplicationListener=\
        //org.springframework.boot.context.ApplicationPidFileWriter
        //1.2.3.

        //spring:
        //  pid:
        //    file: d:/app.pid
    }

    public void test2() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    //应用运行主目录
    //ApplicationHome提供访问应用程序主目录的途径。尝试为Jar文件、解压缩文件和直接运行的应用程序选择一个合理的主目录。
    public void test3() {
        ApplicationHome home = new ApplicationHome();
        System.out.printf("dir: %s, source: %s%n", home.getDir(), home.getSource());

    }

    // 获取Java版本
    //要想知道当前SpringBoot运行时的java版本可以通过JavaVersion获取：
    public void test4() {
        System.out.printf("Java Version: %s%n", JavaVersion.getJavaVersion());

    }

    //应用临时目录
    //ApplicationTemp类提供了访问应用程序特定的临时目录的功能。一般来说，不同的Spring Boot应用程序将得到不同的位置，但是，只需重新启动应用程序即可获得相同的位置。
    public void test5() {
        ApplicationTemp temp = new ApplicationTemp();
        System.out.printf("临时目录: %s%n", temp.getDir());

    }

    // 系统属性/环境变量访问
    //当你需要访问系统属性时可以通过SystemProperties类非常方便的获取。如果你访问的属性不存在时（null），那么它会再从环境变量中获取(System#getenv)。
    public void test6() {
        System.out.printf("java.home=%s%n", SystemProperties.get("java.home"));

    }

    //实例化对象
    //Instantiator通过注入可用参数来实例化对象的简单工厂。
    public static class CLAZZ {

        public static interface DAO {

        }

        public static class A implements DAO {

        }

        public static class B implements DAO {

        }
    }

    //注备上面几个类，接下通过Instantiator一次性实例化多个对象。
    public void test7() {
        Instantiator<DAO> instant = new Instantiator<>(DAO.class, p -> {
        });
        List<DAO> ret = instant.instantiate(List.of("com.pack.A", "com.pack.B"));
        System.out.printf("%s%n", ret);
    }

    //7. 资源加载
    //如果你想将后缀为.properties，.xml，.yaml资源文件加载，那么你可以使用PropertiesPropertySourceLoader与YamlPropertySourceLoader。
    //通过上面2个Loader非常方便的将资源文件加载，加载后的List还可以注册到Environment中，在系统中直接访问。
    public void test8() throws IOException {
        // 加载properties文件
        PropertiesPropertySourceLoader propertyLoader = new PropertiesPropertySourceLoader();
        List<PropertySource<?>> list = propertyLoader.load("pack", new ClassPathResource("pack.properties"));
        System.out.printf("pack.*: %s%n", list.get(0).getSource());
// 加载yaml文件
        YamlPropertySourceLoader yamlLoader = new YamlPropertySourceLoader();
        List<PropertySource<?>> yamls = yamlLoader.load("pack", new ClassPathResource("pack.yml"));
        System.out.printf("pack.*: %s%n", yamls.get(0).getSource());

    }


    //8. 获取basePackages
    private ConfigurableApplicationContext context;

    public void test9() {
        System.out.printf("basepPckages: %s%n", AutoConfigurationPackages.get(context));

    }
}
