package com.kuma.boot.mcp.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * JSON-RPC 2.0 报文定义（MCP 的传输承载格式）。
 *
 * @see <a href="https://www.jsonrpc.org/specification">JSON-RPC 2.0 Specification</a>
 */
public final class JsonRpc {

    private JsonRpc() {
    }

    public static final String VERSION = "2.0";

    // ── 标准错误码 ───────────────────────────────────────────────────────────
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;

    /**
     * 请求 / 通知。{@code id} 为空表示通知（notification），服务端不应返回响应。
     *
     * @param jsonrpc 固定 "2.0"
     * @param id      请求标识（String / Number），通知时为 null
     * @param method  方法名
     * @param params  方法参数
     */
    public record Request(String jsonrpc, Object id, String method, Map<String, Object> params) {
    }

    /** 错误对象 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Error(int code, String message, Object data) {
    }

    /** 响应：{@code result} 与 {@code error} 二者必居其一 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Response(String jsonrpc, Object id, Object result, Error error) {

        public static Response ok(Object id, Object result) {
            return new Response(VERSION, id, result, null);
        }

        public static Response error(Object id, int code, String message) {
            return new Response(VERSION, id, null, new Error(code, message, null));
        }
    }
}
