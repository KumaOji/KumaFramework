/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.io.IoUtil
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  io.swagger.v3.core.jackson.TypeNameResolver
 *  io.swagger.v3.core.util.AnnotationsUtils
 *  io.swagger.v3.oas.annotations.security.SecurityRequirement
 *  io.swagger.v3.oas.annotations.tags.Tag
 *  io.swagger.v3.oas.annotations.tags.Tags
 *  io.swagger.v3.oas.models.Components
 *  io.swagger.v3.oas.models.OpenAPI
 *  io.swagger.v3.oas.models.Operation
 *  io.swagger.v3.oas.models.Paths
 *  io.swagger.v3.oas.models.tags.Tag
 *  org.springdoc.core.customizers.OpenApiBuilderCustomizer
 *  org.springdoc.core.customizers.ServerBaseUrlCustomizer
 *  org.springdoc.core.properties.SpringDocConfigProperties
 *  org.springdoc.core.providers.JavadocProvider
 *  org.springdoc.core.service.OpenAPIService
 *  org.springdoc.core.service.SecurityService
 *  org.springdoc.core.utils.PropertyResolverUtils
 *  org.springframework.context.ApplicationContext
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.web.method.HandlerMethod
 */
package com.kuma.boot.springdoc.springdocextension.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import com.kuma.boot.common.utils.lang.StringUtils;
import io.swagger.v3.core.jackson.TypeNameResolver;
import io.swagger.v3.core.util.AnnotationsUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.Paths;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;

public class OpenApiHandler
extends OpenAPIService {
    private static Class<?> basicErrorController;
    private final SecurityService securityParser;
    private final Map<String, Object> mappingsMap = new HashMap<String, Object>();
    private final Map<HandlerMethod, io.swagger.v3.oas.models.tags.Tag> springdocTags = new HashMap<HandlerMethod, io.swagger.v3.oas.models.tags.Tag>();
    private final Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers;
    private final Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers;
    private final SpringDocConfigProperties springDocConfigProperties;
    private final Map<String, OpenAPI> cachedOpenAPI = new HashMap<String, OpenAPI>();
    private final PropertyResolverUtils propertyResolverUtils;
    private final Optional<JavadocProvider> javadocProvider;
    private ApplicationContext context;
    private OpenAPI openAPI;
    private boolean isServersPresent;
    private String serverBaseUrl;

    public OpenApiHandler(Optional<OpenAPI> openAPI, SecurityService securityParser, SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils, Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers, Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers, Optional<JavadocProvider> javadocProvider) {
        super(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
        if (openAPI.isPresent()) {
            this.openAPI = openAPI.get();
            if (this.openAPI.getComponents() == null) {
                this.openAPI.setComponents(new Components());
            }
            if (this.openAPI.getPaths() == null) {
                this.openAPI.setPaths(new Paths());
            }
            if (CollUtil.isNotEmpty((Collection)this.openAPI.getServers())) {
                this.isServersPresent = true;
            }
        }
        this.propertyResolverUtils = propertyResolverUtils;
        this.securityParser = securityParser;
        this.springDocConfigProperties = springDocConfigProperties;
        this.openApiBuilderCustomisers = openApiBuilderCustomizers;
        this.serverBaseUrlCustomizers = serverBaseUrlCustomizers;
        this.javadocProvider = javadocProvider;
        if (springDocConfigProperties.isUseFqn()) {
            TypeNameResolver.std.setUseFqn(true);
        }
    }

    public Operation buildTags(HandlerMethod handlerMethod, Operation operation, OpenAPI openAPI, Locale locale) {
        SecurityRequirement[] securityRequirements;
        HashSet<io.swagger.v3.oas.models.tags.Tag> tags = new HashSet<io.swagger.v3.oas.models.tags.Tag>();
        Set<String> tagsStr = new HashSet<String>();
        this.buildTagsFromMethod(handlerMethod.getMethod(), tags, tagsStr, locale);
        this.buildTagsFromClass(handlerMethod.getBeanType(), tags, tagsStr, locale);
        if (CollUtil.isNotEmpty(tagsStr)) {
            tagsStr = tagsStr.stream().map(str -> this.propertyResolverUtils.resolve(str, locale)).collect(Collectors.toSet());
        }
        if (this.springdocTags.containsKey(handlerMethod)) {
            io.swagger.v3.oas.models.tags.Tag tag = this.springdocTags.get(handlerMethod);
            tagsStr.add(tag.getName());
            if (openAPI.getTags() == null || !openAPI.getTags().contains(tag)) {
                openAPI.addTagsItem(tag);
            }
        }
        if (CollUtil.isNotEmpty(tagsStr)) {
            if (CollUtil.isEmpty((Collection)operation.getTags())) {
                operation.setTags(new ArrayList<String>(tagsStr));
            } else {
                HashSet<String> operationTagsSet = new HashSet<String>(operation.getTags());
                operationTagsSet.addAll(tagsStr);
                operation.getTags().clear();
                operation.getTags().addAll(operationTagsSet);
            }
        }
        if (this.isAutoTagClasses(operation)) {
            if (this.javadocProvider.isPresent()) {
                String description = this.javadocProvider.get().getClassJavadoc(handlerMethod.getBeanType());
                if (StringUtils.isNotBlank((String)description)) {
                    io.swagger.v3.oas.models.tags.Tag tag = new io.swagger.v3.oas.models.tags.Tag();
                    List list = (List)IoUtil.readLines((Reader)new StringReader(description), new ArrayList());
                    tag.setName((String)list.get(0));
                    operation.addTagsItem((String)list.get(0));
                    tag.setDescription(description);
                    if (openAPI.getTags() == null || !openAPI.getTags().contains(tag)) {
                        openAPI.addTagsItem(tag);
                    }
                }
            } else {
                String tagAutoName = OpenApiHandler.splitCamelCase((String)handlerMethod.getBeanType().getSimpleName());
                operation.addTagsItem(tagAutoName);
            }
        }
        if (CollUtil.isNotEmpty(tags)) {
            List openApiTags = openAPI.getTags();
            if (CollUtil.isNotEmpty((Collection)openApiTags)) {
                tags.addAll(openApiTags);
            }
            openAPI.setTags(new ArrayList<io.swagger.v3.oas.models.tags.Tag>(tags));
        }
        if ((securityRequirements = this.securityParser.getSecurityRequirements(handlerMethod)) != null) {
            if (securityRequirements.length == 0) {
                operation.setSecurity(Collections.emptyList());
            } else {
                this.securityParser.buildSecurityRequirement(securityRequirements, operation);
            }
        }
        return operation;
    }

    private void buildTagsFromMethod(Method method, Set<io.swagger.v3.oas.models.tags.Tag> tags, Set<String> tagsStr, Locale locale) {
        Set tagsSet = AnnotatedElementUtils.findAllMergedAnnotations((AnnotatedElement)method, Tags.class);
        Set methodTags = tagsSet.stream().flatMap(x -> Stream.of(x.value())).collect(Collectors.toSet());
        methodTags.addAll(AnnotatedElementUtils.findAllMergedAnnotations((AnnotatedElement)method, Tag.class));
        if (CollUtil.isNotEmpty(methodTags)) {
            tagsStr.addAll(OpenApiHandler.toSet(methodTags, tag -> this.propertyResolverUtils.resolve(tag.name(), locale)));
            ArrayList<Tag> allTags = new ArrayList<Tag>(methodTags);
            this.addTags(allTags, tags, locale);
        }
    }

    private void addTags(List<Tag> sourceTags, Set<io.swagger.v3.oas.models.tags.Tag> tags, Locale locale) {
        Optional optionalTagSet = AnnotationsUtils.getTags((Tag[])sourceTags.toArray(new Tag[0]), (boolean)true);
        optionalTagSet.ifPresent(tagsSet -> tagsSet.forEach(tag -> {
            tag.name(this.propertyResolverUtils.resolve(tag.getName(), locale));
            tag.description(this.propertyResolverUtils.resolve(tag.getDescription(), locale));
            if (tags.stream().noneMatch(t -> t.getName().equals(tag.getName()))) {
                tags.add((io.swagger.v3.oas.models.tags.Tag)tag);
            }
        }));
    }

    public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
        if (CollUtil.isEmpty(collection) || function == null) {
            return CollUtil.newHashSet((Object[])new Object[0]);
        }
        return collection.stream().map(function).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}

