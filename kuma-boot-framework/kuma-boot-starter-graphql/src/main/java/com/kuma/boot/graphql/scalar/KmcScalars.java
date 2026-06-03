/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.graphql.scalar;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Kuma 扩展 GraphQL 标量定义.
 *
 * <p>提供 Java 常用类型到 GraphQL 标量的映射：
 * <ul>
 *   <li>{@code Long} — 64 位整数，避免 JS Number 精度丢失问题（以字符串传输）</li>
 *   <li>{@code BigDecimal} — 任意精度数值（以字符串传输）</li>
 *   <li>{@code LocalDate} — 本地日期，格式 {@code yyyy-MM-dd}</li>
 *   <li>{@code LocalDateTime} — 本地日期时间，格式 {@code yyyy-MM-dd'T'HH:mm:ss}</li>
 * </ul>
 *
 * @author kuma
 */
public final class KmcScalars {

    private KmcScalars() {}

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // ── Long ─────────────────────────────────────────────────────────────────

    /**
     * 64 位整数标量 {@code Long}.
     * 序列化时输出字符串，避免前端 JS 大数精度丢失；
     * 解析时同时接受字符串和整数。
     */
    public static final GraphQLScalarType LONG = GraphQLScalarType.newScalar()
            .name("Long")
            .description("64-bit signed integer serialized as String to preserve JS precision")
            .coercing(new Coercing<Long, String>() {

                @Override
                public String serialize(Object result, GraphQLContext ctx, Locale locale) {
                    if (result instanceof Long l)   return l.toString();
                    if (result instanceof Number n) return Long.toString(n.longValue());
                    if (result instanceof String s) {
                        try { return Long.toString(Long.parseLong(s)); }
                        catch (NumberFormatException e) {
                            throw new CoercingSerializeException("Not a valid Long: " + s);
                        }
                    }
                    throw new CoercingSerializeException("Cannot serialize " + result + " as Long");
                }

                @Override
                public Long parseValue(Object input, GraphQLContext ctx, Locale locale) {
                    if (input instanceof Long l)    return l;
                    if (input instanceof Number n)  return n.longValue();
                    if (input instanceof String s) {
                        try { return Long.parseLong(s); }
                        catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Not a valid Long: " + s);
                        }
                    }
                    throw new CoercingParseValueException("Cannot parse Long from: " + input);
                }

                @Override
                public Long parseLiteral(Value<?> input, CoercedVariables vars, GraphQLContext ctx, Locale locale) {
                    if (input instanceof graphql.language.IntValue iv)    return iv.getValue().longValueExact();
                    if (input instanceof StringValue sv) {
                        try { return Long.parseLong(sv.getValue()); }
                        catch (NumberFormatException e) {
                            throw new graphql.schema.CoercingParseLiteralException("Not a valid Long literal: " + sv.getValue());
                        }
                    }
                    throw new graphql.schema.CoercingParseLiteralException("Cannot parse Long from literal: " + input);
                }
            })
            .build();

    // ── BigDecimal ────────────────────────────────────────────────────────────

    /** 任意精度数值标量 {@code BigDecimal}，以字符串传输保证精度. */
    public static final GraphQLScalarType BIG_DECIMAL = GraphQLScalarType.newScalar()
            .name("BigDecimal")
            .description("Arbitrary-precision decimal number, serialized as String")
            .coercing(new Coercing<BigDecimal, String>() {

                @Override
                public String serialize(Object result, GraphQLContext ctx, Locale locale) {
                    if (result instanceof BigDecimal bd) return bd.toPlainString();
                    if (result instanceof Number n)      return new BigDecimal(n.toString()).toPlainString();
                    if (result instanceof String s) {
                        try { return new BigDecimal(s).toPlainString(); }
                        catch (NumberFormatException e) {
                            throw new CoercingSerializeException("Not a valid BigDecimal: " + s);
                        }
                    }
                    throw new CoercingSerializeException("Cannot serialize as BigDecimal: " + result);
                }

                @Override
                public BigDecimal parseValue(Object input, GraphQLContext ctx, Locale locale) {
                    if (input instanceof BigDecimal bd) return bd;
                    if (input instanceof Number n)      return new BigDecimal(n.toString());
                    if (input instanceof String s) {
                        try { return new BigDecimal(s); }
                        catch (NumberFormatException e) {
                            throw new CoercingParseValueException("Not a valid BigDecimal: " + s);
                        }
                    }
                    throw new CoercingParseValueException("Cannot parse BigDecimal from: " + input);
                }

                @Override
                public BigDecimal parseLiteral(Value<?> input, CoercedVariables vars, GraphQLContext ctx, Locale locale) {
                    if (input instanceof graphql.language.FloatValue fv) return fv.getValue();
                    if (input instanceof graphql.language.IntValue iv)   return new BigDecimal(iv.getValue());
                    if (input instanceof StringValue sv) {
                        try { return new BigDecimal(sv.getValue()); }
                        catch (NumberFormatException e) {
                            throw new graphql.schema.CoercingParseLiteralException("Not a valid BigDecimal literal: " + sv.getValue());
                        }
                    }
                    throw new graphql.schema.CoercingParseLiteralException("Cannot parse BigDecimal from literal: " + input);
                }
            })
            .build();

    // ── LocalDate ─────────────────────────────────────────────────────────────

    /** 本地日期标量 {@code LocalDate}，格式 {@code yyyy-MM-dd}. */
    public static final GraphQLScalarType LOCAL_DATE = GraphQLScalarType.newScalar()
            .name("LocalDate")
            .description("Local date, format yyyy-MM-dd")
            .coercing(new Coercing<LocalDate, String>() {

                @Override
                public String serialize(Object result, GraphQLContext ctx, Locale locale) {
                    if (result instanceof LocalDate ld) return ld.format(DATE_FMT);
                    if (result instanceof String s)     return LocalDate.parse(s, DATE_FMT).format(DATE_FMT);
                    throw new CoercingSerializeException("Cannot serialize as LocalDate: " + result);
                }

                @Override
                public LocalDate parseValue(Object input, GraphQLContext ctx, Locale locale) {
                    if (input instanceof LocalDate ld) return ld;
                    if (input instanceof String s) {
                        try { return LocalDate.parse(s, DATE_FMT); }
                        catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid LocalDate (expected yyyy-MM-dd): " + s);
                        }
                    }
                    throw new CoercingParseValueException("Cannot parse LocalDate from: " + input);
                }

                @Override
                public LocalDate parseLiteral(Value<?> input, CoercedVariables vars, GraphQLContext ctx, Locale locale) {
                    if (input instanceof StringValue sv) {
                        try { return LocalDate.parse(sv.getValue(), DATE_FMT); }
                        catch (DateTimeParseException e) {
                            throw new graphql.schema.CoercingParseLiteralException("Invalid LocalDate literal: " + sv.getValue());
                        }
                    }
                    throw new graphql.schema.CoercingParseLiteralException("Cannot parse LocalDate from literal: " + input);
                }
            })
            .build();

    // ── LocalDateTime ─────────────────────────────────────────────────────────

    /** 本地日期时间标量 {@code LocalDateTime}，格式 {@code yyyy-MM-dd'T'HH:mm:ss}. */
    public static final GraphQLScalarType LOCAL_DATE_TIME = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("Local date-time, format yyyy-MM-dd'T'HH:mm:ss")
            .coercing(new Coercing<LocalDateTime, String>() {

                @Override
                public String serialize(Object result, GraphQLContext ctx, Locale locale) {
                    if (result instanceof LocalDateTime ldt) return ldt.format(DATETIME_FMT);
                    if (result instanceof String s)          return LocalDateTime.parse(s, DATETIME_FMT).format(DATETIME_FMT);
                    throw new CoercingSerializeException("Cannot serialize as LocalDateTime: " + result);
                }

                @Override
                public LocalDateTime parseValue(Object input, GraphQLContext ctx, Locale locale) {
                    if (input instanceof LocalDateTime ldt) return ldt;
                    if (input instanceof String s) {
                        try { return LocalDateTime.parse(s, DATETIME_FMT); }
                        catch (DateTimeParseException e) {
                            throw new CoercingParseValueException("Invalid LocalDateTime (expected yyyy-MM-dd'T'HH:mm:ss): " + s);
                        }
                    }
                    throw new CoercingParseValueException("Cannot parse LocalDateTime from: " + input);
                }

                @Override
                public LocalDateTime parseLiteral(Value<?> input, CoercedVariables vars, GraphQLContext ctx, Locale locale) {
                    if (input instanceof StringValue sv) {
                        try { return LocalDateTime.parse(sv.getValue(), DATETIME_FMT); }
                        catch (DateTimeParseException e) {
                            throw new graphql.schema.CoercingParseLiteralException("Invalid LocalDateTime literal: " + sv.getValue());
                        }
                    }
                    throw new graphql.schema.CoercingParseLiteralException("Cannot parse LocalDateTime from literal: " + input);
                }
            })
            .build();
}
