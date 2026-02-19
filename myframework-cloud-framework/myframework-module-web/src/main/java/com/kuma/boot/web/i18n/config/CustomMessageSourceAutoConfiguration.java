/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.AutoConfigureOrder
 *  org.springframework.boot.autoconfigure.condition.ConditionMessage
 *  org.springframework.boot.autoconfigure.condition.ConditionMessage$Builder
 *  org.springframework.boot.autoconfigure.condition.ConditionOutcome
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.SearchStrategy
 *  org.springframework.boot.autoconfigure.condition.SpringBootCondition
 *  org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
 *  org.springframework.boot.autoconfigure.context.MessageSourceProperties
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.MessageSource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.StringUtils
 *  org.springframework.web.servlet.LocaleResolver
 *  org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver
 */
package com.kuma.boot.web.i18n.config;

import com.kuma.boot.web.i18n.WildcardReloadableResourceBundleMessageSource;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@AutoConfiguration(before={MessageSourceAutoConfiguration.class})
@ConditionalOnMissingBean(name={"messageSource"}, search=SearchStrategy.CURRENT)
@AutoConfigureOrder(value=-2147483648)
@Conditional(value={ResourceBundleCondition.class})
@EnableConfigurationProperties
public class CustomMessageSourceAutoConfiguration {
    private static final Resource[] NO_RESOURCES = new Resource[0];

    @Bean
    @ConfigurationProperties(prefix="spring.messages")
    @ConditionalOnMissingBean
    public MessageSourceProperties messageSourceProperties() {
        return new MessageSourceProperties();
    }

    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {
        WildcardReloadableResourceBundleMessageSource messageSource = new WildcardReloadableResourceBundleMessageSource();
        if (!CollectionUtils.isEmpty((Collection)properties.getBasename())) {
            messageSource.setBasenames(StringUtils.commaDelimitedListToStringArray((String)properties.getBasename().stream().map(StringUtils::trimAllWhitespace).collect(Collectors.joining(","))));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(Arrays.asList(Locale.CHINA, Locale.US));
        resolver.setDefaultLocale(Locale.CHINA);
        return resolver;
    }

    protected static class ResourceBundleCondition
    extends SpringBootCondition {
        private static ConcurrentReferenceHashMap<String, ConditionOutcome> cache = new ConcurrentReferenceHashMap();

        protected ResourceBundleCondition() {
        }

        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String basename = context.getEnvironment().getProperty("spring.messages.basename", "messages");
            ConditionOutcome outcome = (ConditionOutcome)cache.get((Object)basename);
            if (outcome == null) {
                outcome = this.getMatchOutcomeForBasename(context, basename);
                cache.put((Object)basename, (Object)outcome);
            }
            return outcome;
        }

        private ConditionOutcome getMatchOutcomeForBasename(ConditionContext context, String basename) {
            ConditionMessage.Builder message = ConditionMessage.forCondition((String)"ResourceBundle", (Object[])new Object[0]);
            for (String name : StringUtils.commaDelimitedListToStringArray((String)StringUtils.trimAllWhitespace((String)basename))) {
                for (Resource resource : this.getResources(context.getClassLoader(), name)) {
                    if (!resource.exists()) continue;
                    return ConditionOutcome.match((ConditionMessage)message.found("bundle").items(new Object[]{resource}));
                }
            }
            return ConditionOutcome.noMatch((ConditionMessage)message.didNotFind("bundle with basename " + basename).atAll());
        }

        private Resource[] getResources(ClassLoader classLoader, String name) {
            String target = name.replace('.', '/');
            try {
                return new PathMatchingResourcePatternResolver(classLoader).getResources("classpath*:" + target + ".properties");
            }
            catch (Exception ex) {
                return NO_RESOURCES;
            }
        }
    }
}

