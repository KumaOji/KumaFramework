package com.kuma.boot.core.runtime.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import com.google.common.collect.Lists;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 延迟导出的ConsumerBean 该类的getObject方法会返回一个自定义的代理对象
 */
public class DelayConsumerBean<T> implements InitializingBean,
        FactoryBean<T>, ApplicationContextAware, DisposableBean, BeanNameAware {

    private static final long serialVersionUID = 6835324481364430812L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayConsumerBean.class);

    private ApplicationContext applicationContext;

    protected transient String beanName;

    private transient T object;

    private transient Class objectType;

    public static final List<Future<?>> REFER_FUTURE_LIST = Lists.newArrayList();

    public static final ThreadPoolExecutor CONSUMER_REFER_EXECUTOR = new ThreadPoolExecutor(
            32, 32,
            60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(102400),

            new ThreadPoolExecutor.CallerRunsPolicy());

    @JSONField(
            serialize = false
    )
    @SuppressWarnings("unchecked")
    public T getObject() throws Exception {
//        Class<T> consumerInterfaceClass = ClassLoaderUtils.forName(this.interfaceId);
//        //先生成一个代理对象
//        T delayConsumer = (T) Proxy.newProxyInstance(
//                consumerInterfaceClass.getClassLoader(),
//                new Class[]{consumerInterfaceClass},
//                new DelayConsumerInvocationHandler()
//        );
//
//        //使用异步线程refer
//        REFER_FUTURE_LIST.add(CONSUMER_REFER_EXECUTOR.submit(() -> {
//            super.refer();
//        }));
//
//        object = CommonUtils.isUnitTestMode() ? null : delayConsumer;
        //返回提前生成的代理对象
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void setBeanName( String name ) {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * DelayConsumerInvocationHandler
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    private class DelayConsumerInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
//            if (method.getDeclaringClass() == Object.class) {
//                return method.invoke(DelayConsumerBean.this, args);
//            }
//            if (DelayConsumerBean.this.proxyIns == null) {
//                throw new RuntimeException("DelayConsumerBean.this.proxyIns is null");
//            }
//            try {
//                //客户端发起调用后，触发真实的consumer代理执行
//                return method.invoke(DelayConsumerBean.this.proxyIns, args);
//            } catch (InvocationTargetException exp) {
//                throw exp.getTargetException();
//            } catch (Exception ex) {
//                throw ApplicationException.mirrorOf(ex);
//            }
            return null;
        }
    }

    /**
     * 等待所有的任务完成，确保所有的consumer都refer成功了
     */
    public static void waitForRefer( ContextRefreshedEvent contextRefreshedEvent ) {
        CONSUMER_REFER_EXECUTOR.allowCoreThreadTimeOut(true);
        LOGGER.info("==> Ready for JSF consumer 2 refer! Application name: {} count:{}",
                contextRefreshedEvent.getApplicationContext().getApplicationName(), REFER_FUTURE_LIST.size());
        try {
            for (Future<?> future : REFER_FUTURE_LIST) {
                future.get();
            }
        } catch (Exception exp) {
            // 在Web应用中，容器可能是一个父子容器，因此关闭上下文时需要递归往上遍历，把父容器也一起关闭
            //LOGGER.error("<== Exception while batch exporting JSF provider!", exp instanceof ApplicationException ? exp.getCause() : exp);
            ApplicationContext toClose = contextRefreshedEvent.getApplicationContext();
            while (toClose instanceof ConfigurableApplicationContext) {
                ApplicationContext parentApplicationContext = toClose.getParent();
                try {
                    ( (ConfigurableApplicationContext) toClose ).close();
                } catch (Exception closeExp) {
                    LOGGER.error("<== Exception while close application context: {}", toClose.getApplicationName(),
                            closeExp);
                }
                toClose = parentApplicationContext;
            }
        }
    }


    protected DelayConsumerBean() {
    }

    public void setApplicationContext( ApplicationContext appContext ) throws BeansException {
        this.applicationContext = appContext;
    }

}
