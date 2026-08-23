package com.kuma.cloud.lab.leetcode.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * LeetCode 题目运行参数。
 */
@Data
public class LeetCodeRunDTO {

    @NotNull(message = "input 不能为空")
    private Map<String, Object> input;

}
