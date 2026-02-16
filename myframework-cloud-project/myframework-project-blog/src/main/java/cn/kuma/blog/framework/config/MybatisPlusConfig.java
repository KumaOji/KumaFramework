package cn.kuma.blog.framework.config;

import cn.kuma.blog.framework.mybatisplus.interceptor.SchemaSwitchInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@MapperScan("cn.kuma.blog.**.mapper")
public class MybatisPlusConfig {

    @Value("${schema.default:blog}")
    private String schemaDefault;

    /**
     * 添加分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new AutoPaginationInnerInterceptor()); // 如果配置多个插件,
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    @Bean
    public Interceptor schemaSwitchInterceptor() {
        SchemaSwitchInterceptor interceptor = new SchemaSwitchInterceptor();
        interceptor.setDefaultSchema(schemaDefault);
        return interceptor;
    }

    /**
     * 将 Schema 切换拦截器注册到 MyBatis（在 SqlSessionFactory 初始化后添加）
     * 使用 static 工厂方法，避免 BeanPostProcessor 未被全部处理器处理的警告
     */
    @Bean
    public static BeanPostProcessor schemaSwitchInterceptorRegistrar(Environment environment) {
        String schemaDefault = environment.getProperty("schema.default", "blog");
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof SqlSessionFactory sqlSessionFactory) {
                    // 检查是否已经添加了 schemaSwitchInterceptor
                    boolean alreadyAdded = sqlSessionFactory.getConfiguration().getInterceptors().stream()
                            .anyMatch(interceptor -> interceptor instanceof SchemaSwitchInterceptor);
                    if (!alreadyAdded) {
                        SchemaSwitchInterceptor interceptor = new SchemaSwitchInterceptor();
                        interceptor.setDefaultSchema(schemaDefault);
                        sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
                    }
                }
                return bean;
            }
        };
    }

    // 自动选择方言
    public static class AutoPaginationInnerInterceptor extends PaginationInnerInterceptor {
        @Override
        protected IDialect findIDialect(Executor executor) {
            return DialectFactory.getDialect(JdbcUtils.getDbType(executor));
        }
    }

}
