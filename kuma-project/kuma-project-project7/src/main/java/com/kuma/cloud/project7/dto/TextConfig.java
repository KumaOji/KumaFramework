package com.kuma.cloud.project7.dto;

import com.kuma.cloud.ccsr.client.listener.ConfigData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 简单文本配置，用于演示向 CCSR 写入字符串内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextConfig implements ConfigData {

    private String content;
}
