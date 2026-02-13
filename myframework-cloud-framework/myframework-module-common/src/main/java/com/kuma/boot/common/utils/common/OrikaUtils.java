/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ma.glasnost.orika.Converter
 *  ma.glasnost.orika.DefaultFieldMapper
 *  ma.glasnost.orika.MapperFacade
 *  ma.glasnost.orika.MapperFactory
 *  ma.glasnost.orika.MappingContext
 *  ma.glasnost.orika.converter.BidirectionalConverter
 *  ma.glasnost.orika.impl.DefaultMapperFactory$Builder
 *  ma.glasnost.orika.metadata.ClassMapBuilder
 *  ma.glasnost.orika.metadata.Type
 *  ma.glasnost.orika.metadata.TypeFactory
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.log.LogUtils;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.DefaultFieldMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

public final class OrikaUtils {
    private static final MapperFactory MAPPER_FACTORY = ((DefaultMapperFactory.Builder)new DefaultMapperFactory.Builder().mapNulls(false)).build();
    private static final MapperFacade MAPPER_FACADE;
    private static final Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP;

    public static <S, D> D convert(S source, Class<D> targetClass) {
        return (D)MAPPER_FACADE.map(source, targetClass);
    }

    public static <S, D> D convert(S source, Class<D> targetClass, Map<String, String> configMap) {
        MapperFacade mapperFacade = OrikaUtils.getMapperFacade(targetClass, source.getClass(), configMap);
        return (D)mapperFacade.map(source, targetClass);
    }

    public static <S, D> void copy(S source, D target) {
        MAPPER_FACADE.map(source, target);
    }

    public static <S, D> List<D> converts(Iterable<S> sources, Class<D> targetClass) {
        return MAPPER_FACADE.mapAsList(sources, targetClass);
    }

    public static <S, D> List<D> converts(Collection<S> source, Class<D> targetClass, Map<String, String> configMap) {
        if (source.iterator().next() == null) {
            return null;
        }
        MapperFacade mapperFacade = OrikaUtils.getMapperFacade(targetClass, source.iterator().next().getClass(), configMap);
        return mapperFacade.mapAsList(source, targetClass);
    }

    public static <S, D> List<D> converts(Collection<S> source, Class<S> sourceClass, Class<D> targetClass, Map<String, String> configMap) {
        MapperFacade mapperFacade = OrikaUtils.getMapperFacade(targetClass, sourceClass, configMap);
        return mapperFacade.mapAsList(source, targetClass);
    }

    private static <S, D> MapperFacade getMapperFacade(Class<S> sourceClass, Class<D> targetClass, Map<String, String> configMap) {
        String mapKey = targetClass.getCanonicalName() + "_" + sourceClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            ClassMapBuilder<D, S> classMapBuilder = OrikaUtils.classMap(sourceClass, targetClass);
            configMap.forEach((arg_0, arg_1) -> classMapBuilder.field(arg_0, arg_1));
            classMapBuilder.byDefault(new DefaultFieldMapper[0]).register();
            mapperFacade = MAPPER_FACTORY.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }

    public static <S, D> D[] convertArray(S[] source, D[] target, Class<D> targetClass) {
        return MAPPER_FACADE.mapAsArray((Object[])target, (Object[])source, targetClass);
    }

    public static <S, D> ClassMapBuilder<D, S> classMap(Class<S> source, Class<D> target) {
        return MAPPER_FACTORY.classMap(target, source);
    }

    public static <S> Type<S> getType(Class<S> rawType) {
        return TypeFactory.valueOf(rawType);
    }

    public static <S, D> D map(S source, Class<D> destinationClass) {
        return (D)MAPPER_FACADE.map(source, destinationClass);
    }

    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return (D)MAPPER_FACADE.map(source, sourceType, destinationType);
    }

    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return MAPPER_FACADE.mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    }

    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return MAPPER_FACADE.mapAsList(sourceList, sourceType, destinationType);
    }

    public static <S, D> D[] mapArray(D[] destination, S[] source, Class<D> destinationClass) {
        return MAPPER_FACADE.mapAsArray((Object[])destination, (Object[])source, destinationClass);
    }

    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return MAPPER_FACADE.mapAsArray((Object[])destination, (Object[])source, sourceType, destinationType);
    }

    static {
        MAPPER_FACTORY.getConverterFactory().registerConverter((Converter)new DateToString());
        MAPPER_FACTORY.getConverterFactory().registerConverter((Converter)new TimestampToString());
        MAPPER_FACTORY.getConverterFactory().registerConverter((Converter)new LocalDateTimeToString());
        MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();
        CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<String, MapperFacade>();
    }

    public static class DateToString
    extends BidirectionalConverter<Date, String> {
        public String convertTo(Date date, Type<String> type, MappingContext mappingContext) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }

        public Date convertFrom(String s, Type<Date> type, MappingContext mappingContext) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return format.parse(s);
            }
            catch (ParseException e) {
                LogUtils.error(e);
                return null;
            }
        }
    }

    public static class TimestampToString
    extends BidirectionalConverter<Timestamp, String> {
        public String convertTo(Timestamp date, Type<String> type, MappingContext mappingContext) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return format.format(date);
        }

        public Timestamp convertFrom(String s, Type<Timestamp> type, MappingContext mappingContext) {
            return null;
        }
    }

    public static class LocalDateTimeToString
    extends BidirectionalConverter<LocalDateTime, String> {
        public String convertTo(LocalDateTime dateTime, Type<String> type, MappingContext mappingContext) {
            return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        public LocalDateTime convertFrom(String s, Type<LocalDateTime> type, MappingContext mappingContext) {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}

