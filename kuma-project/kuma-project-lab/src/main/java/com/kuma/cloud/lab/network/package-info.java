/**
 * Webhook 与 Socket 学习：运行 NetworkLearningDemo.main()。
 * Webhook 是事件方主动调用接收方 HTTP 端点；Socket 是底层通信 API。
 * TCP 不保留应用消息边界；UDP 保留数据报边界但不保证送达、顺序或去重。
 * 示例仅使用回环地址和临时端口，详细步骤见 docs/network-labs.md。
 * SocketLifecycleLesson 补充长连接、连接内状态、半关闭和重连；HTTP Session 在 session 包的独立 Servlet 容器中学习。
 */
package com.kuma.cloud.lab.network;
