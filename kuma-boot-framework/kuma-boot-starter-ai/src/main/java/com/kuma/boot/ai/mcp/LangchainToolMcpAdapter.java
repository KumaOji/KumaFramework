package com.kuma.boot.ai.mcp;

import com.kuma.boot.mcp.protocol.McpSchema;
import com.kuma.boot.mcp.tool.McpTool;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 langchain4j {@link Tool} 标注的方法适配为 MCP 工具（{@link McpTool}）。
 *
 * <p>由 {@link AiToolMcpRegistrar} 为每个 {@code @Tool} 方法注册一个本类实例,使现有 AI 工具
 * 无需改动即可通过 MCP 协议对外暴露。工具名取 {@code @Tool.name()}（缺省用方法名）,描述取
 * {@code @Tool.value()},入参 JSON Schema 由方法参数（{@link P} 提供描述）反射推导。
 */
public class LangchainToolMcpAdapter implements McpTool {

    private static final Logger log = LoggerFactory.getLogger(LangchainToolMcpAdapter.class);

    private final Object target;
    private final Method method;
    private final Class<?>[] paramTypes;
    private final String[] paramNames;
    private final String name;
    private final String description;
    private final Map<String, Object> inputSchema;

    public LangchainToolMcpAdapter(Object target, String methodName, Class<?>[] paramTypes) {
        this.target = target;
        this.paramTypes = paramTypes;
        this.method = resolveMethod(target.getClass(), methodName, paramTypes);
        this.method.setAccessible(true);

        Tool tool = method.getAnnotation(Tool.class);
        this.name = (tool != null && !tool.name().isEmpty()) ? tool.name() : method.getName();
        this.description = (tool != null) ? String.join("\n", tool.value()) : "";

        Parameter[] params = method.getParameters();
        this.paramNames = new String[params.length];
        this.inputSchema = buildSchema(params, paramNames);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Map<String, Object> inputSchema() {
        return inputSchema;
    }

    @Override
    public McpSchema.CallToolResult call(Map<String, Object> arguments) {
        Map<String, Object> args = arguments == null ? Map.of() : arguments;
        try {
            Object[] callArgs = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                callArgs[i] = coerce(args.get(paramNames[i]), paramTypes[i]);
            }
            Object result = method.invoke(target, callArgs);
            return McpSchema.CallToolResult.text(result == null ? "" : String.valueOf(result));
        } catch (Exception e) {
            Throwable cause = (e instanceof InvocationTargetException ite && ite.getCause() != null)
                    ? ite.getCause() : e;
            log.warn("MCP tool '{}' invocation failed: {}", name, cause.getMessage());
            return McpSchema.CallToolResult.error("工具调用失败: " + cause.getMessage());
        }
    }

    // ── 内部工具 ─────────────────────────────────────────────────────────────

    private static Method resolveMethod(Class<?> type, String methodName, Class<?>[] paramTypes) {
        try {
            return type.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("无法解析工具方法: " + type.getName() + "#" + methodName, e);
        }
    }

    /** 由方法参数构建 JSON Schema：{type:object, properties:{...}, required:[...]} */
    private static Map<String, Object> buildSchema(Parameter[] params, String[] outNames) {
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            P p = param.getAnnotation(P.class);
            String pName = param.getName();
            outNames[i] = pName;

            Map<String, Object> prop = new LinkedHashMap<>();
            prop.put("type", jsonType(param.getType()));
            if (p != null && !p.value().isEmpty()) {
                prop.put("description", p.value());
            }
            properties.put(pName, prop);

            // @P.required() 缺省为 true；无 @P 注解时同样视为必填
            if (p == null || p.required()) {
                required.add(pName);
            }
        }
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        if (!required.isEmpty()) {
            schema.put("required", required);
        }
        return schema;
    }

    private static String jsonType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) return "boolean";
        if (type == int.class || type == Integer.class
                || type == long.class || type == Long.class
                || type == short.class || type == Short.class) return "integer";
        if (Number.class.isAssignableFrom(boxed(type)) || type.isPrimitive() && type != boolean.class
                && type != char.class) return "number";
        return "string";
    }

    private static Class<?> boxed(Class<?> type) {
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        return type;
    }

    /** 将客户端传入的 JSON 值强制转换为目标参数类型 */
    private static Object coerce(Object value, Class<?> target) {
        if (value == null) {
            return target.isPrimitive() ? defaultPrimitive(target) : null;
        }
        if (target.isInstance(value)) {
            return value;
        }
        if (target == String.class) {
            return String.valueOf(value);
        }
        if (target == boolean.class || target == Boolean.class) {
            return (value instanceof Boolean b) ? b : Boolean.parseBoolean(value.toString());
        }
        if (value instanceof Number n) {
            return fromNumber(n, target);
        }
        String s = value.toString().trim();
        if (target == double.class || target == Double.class) return Double.parseDouble(s);
        if (target == float.class || target == Float.class) return Float.parseFloat(s);
        if (target == int.class || target == Integer.class) return Integer.parseInt(s);
        if (target == long.class || target == Long.class) return Long.parseLong(s);
        if (target == short.class || target == Short.class) return Short.parseShort(s);
        return value;
    }

    private static Object fromNumber(Number n, Class<?> target) {
        if (target == double.class || target == Double.class) return n.doubleValue();
        if (target == float.class || target == Float.class) return n.floatValue();
        if (target == int.class || target == Integer.class) return n.intValue();
        if (target == long.class || target == Long.class) return n.longValue();
        if (target == short.class || target == Short.class) return n.shortValue();
        return n;
    }

    private static Object defaultPrimitive(Class<?> target) {
        if (target == boolean.class) return false;
        if (target == double.class) return 0d;
        if (target == float.class) return 0f;
        if (target == long.class) return 0L;
        if (target == int.class) return 0;
        if (target == short.class) return (short) 0;
        if (target == byte.class) return (byte) 0;
        if (target == char.class) return '\0';
        return null;
    }
}
