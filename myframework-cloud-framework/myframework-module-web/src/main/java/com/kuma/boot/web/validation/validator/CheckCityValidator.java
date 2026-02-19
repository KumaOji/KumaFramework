/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.io.IORuntimeException
 *  cn.hutool.core.io.IoUtil
 *  cn.hutool.json.JSONUtil
 *  com.kuma.boot.common.enums.CityTypeEnum
 *  com.kuma.boot.common.model.CityEntity
 *  jakarta.validation.ConstraintValidator
 *  jakarta.validation.ConstraintValidatorContext
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.data.redis.core.StringRedisTemplate
 *  org.springframework.util.StringUtils
 */
package com.kuma.boot.web.validation.validator;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.common.enums.CityTypeEnum;
import com.kuma.boot.common.model.CityEntity;
import com.kuma.boot.web.validation.annotation.CheckCityValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

public class CheckCityValidator
implements ConstraintValidator<CheckCityValid, String> {
    private CityTypeEnum type;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void initialize(CheckCityValid annotation) {
        this.type = annotation.value();
    }

    public boolean isValid(String inputValue, ConstraintValidatorContext context) {
        if (inputValue == null) {
            return false;
        }
        String jsonStr = (String)this.redisTemplate.opsForValue().get((Object)"constant:city");
        if (!StringUtils.hasText((String)jsonStr)) {
            try {
                ClassPathResource resource = new ClassPathResource("city.json");
                InputStream inputStream = resource.getInputStream();
                String line = IoUtil.readUtf8((InputStream)inputStream);
                this.redisTemplate.opsForValue().set((Object)"constant:city", (Object)line);
                jsonStr = line;
            }
            catch (IORuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        List cityJson = JSONUtil.toList((String)jsonStr, CityEntity.class);
        return (switch (this.type) {
            case CityTypeEnum.PROVINCE -> cityJson.stream().filter(item -> inputValue.equals(item.getName()) && item.getParent() == null).findFirst().orElse(null);
            default -> cityJson.stream().filter(item -> {
                if (inputValue.equals(item.getName()) && item.getParent() != null) {
                    CityEntity parentEntity = cityJson.stream().filter(parent -> item.getParent().equals(parent.getValue()) && parent.getParent() == null).findFirst().orElse(null);
                    return parentEntity != null;
                }
                return false;
            }).findFirst().orElse(null);
            case CityTypeEnum.AREA -> cityJson.stream().filter(item -> {
                if (inputValue.equals(item.getName()) && item.getParent() != null) {
                    CityEntity parentEntity = cityJson.stream().filter(parent -> item.getParent().equals(parent.getValue()) && parent.getParent() != null).findFirst().orElse(null);
                    return parentEntity != null;
                }
                return false;
            }).findFirst().orElse(null);
        }) != null;
    }
}

