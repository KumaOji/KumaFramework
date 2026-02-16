package cn.kuma.blog.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication(
	scanBasePackages = {"cn.kuma.blog.main", "cn.kuma.blog.common", "cn.kuma.blog.framework", "cn.kuma.blog.python", "com.kuma.boot"}
)
@ConfigurationPropertiesScan
public class MainApplication {

	private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

	public static void main(String[] args) {
		log.info("Blog 应用启动中...");
		SpringApplication app = new SpringApplication(MainApplication.class);
		app.setRegisterShutdownHook(true);
		app.run(args);
		log.info("Blog 应用启动完成");
	}
}
