/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.core.runtime.extension;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringValueResolver;

/**
 * 该类本身并没有扩展点，但是该类内部却有6个扩展点可供实现 ，这些类触发的时机在bean实例化之后，初始化之前
 *
 * 可以看到，该类用于执行各种驱动接口，在bean实例化之后，属性填充之后，通过执行以上红框标出的扩展接口，来获取对应容器的变量。 所以这里应该来说是有6个扩展点，这里就放一起来说了
 *
 * @author kuma
 * @version 2022.09
 * @since 2023-08-07 09:43
 */
public class Test7ApplicationContextAwareProcessor {

    /**
     * 用于获取EnviromentAware的一个扩展类，这个变量非常有用， 可以获得系统内的所有参数。当然个人认为这个Aware没必要去扩展，因为spring内部都可以通过注入的方式来直接获得。
     */
    public class TestEnvironmentAware implements EnvironmentAware {

        @Override
        public void setEnvironment( Environment environment ) {
        }
    }

    // EmbeddedValueResolverAware：用于获取StringValueResolver的一个扩展类，
    // StringValueResolver用于获取基于String类型的properties的变量，一般我们都用@Value的方式去获取，
    // 如果实现了这个Aware接口，把StringValueResolver缓存起来，通过这个类去获取String类型的变量，效果是一样的。
    /**
     * TestEmbeddedValueResolverAware
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public class TestEmbeddedValueResolverAware implements EmbeddedValueResolverAware {

        @Override
        public void setEmbeddedValueResolver( StringValueResolver resolver ) {
        }
    }

    // ResourceLoaderAware：用于获取ResourceLoader的一个扩展类，
    // ResourceLoader可以用于获取classpath内所有的资源对象，可以扩展此类来拿到ResourceLoader对象。
    /**
     * TestResourceLoaderAware
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public class TestResourceLoaderAware implements ResourceLoaderAware {

        @Override
        public void setResourceLoader( ResourceLoader resourceLoader ) {
        }
    }

    // ApplicationEventPublisherAware：用于获取ApplicationEventPublisher的一个扩展类，
    // ApplicationEventPublisher可以用来发布事件，结合ApplicationListener来共同使用，
    // 下文在介绍ApplicationListener时会详细提到。这个对象也可以通过spring注入的方式来获得。
    /**
     * TestApplicationEventPublisherAware
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public class TestApplicationEventPublisherAware implements ApplicationEventPublisherAware {

        @Override
        public void setApplicationEventPublisher( ApplicationEventPublisher applicationEventPublisher ) {
        }
    }

    // MessageSourceAware：用于获取MessageSource的一个扩展类，MessageSource主要用来做国际化。
    /**
     * TestMessageSourceAware
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public class TestMessageSourceAware implements MessageSourceAware {

        @Override
        public void setMessageSource( MessageSource messageSource ) {
        }
    }

    // ApplicationContextAware：用来获取ApplicationContext的一个扩展类，
    // ApplicationContext应该是很多人非常熟悉的一个类了，就是spring上下文管理器，可以手动的获取任何在spring上下文注册的bean，我们经常扩展这个接口来缓存spring上下文，包装成静态方法。
    // 同时ApplicationContext也实现了BeanFactory，MessageSource，ApplicationEventPublisher等接口，也可以用来做相关接口的事情。
    /**
     * TestApplicationContextAware
     *
     * @author kuma
     * @version 2026.01
     * @since 2025-12-19 09:30:45
     */
    public class TestApplicationContextAware implements ApplicationContextAware {

        @Override
        public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {
        }
    }
}
