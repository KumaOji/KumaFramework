package com.kuma.cloud.lab.starter.support;

/**
 * Starter 探测状态。
 */
public enum StarterProbeStatus {

    /** 依赖不在 classpath */
    NOT_ON_CLASSPATH,

    /** 在 classpath 但未注册探测器 */
    ON_CLASSPATH,

    /** Bean 已就绪，尚未执行冒烟测试 */
    READY,

    /** 冒烟测试通过 */
    PASSED,

    /** 诊断或冒烟测试失败 */
    FAILED,

    /** 主动跳过（例如外部依赖未配置） */
    SKIPPED

}
