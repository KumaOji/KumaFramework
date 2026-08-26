package com.kuma.cloud.lab.leetcode.config;

import com.kuma.cloud.lab.leetcode.runner.LeetCodeProblemRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LeetCodeProblemRunnersConfiguration {

    @Bean
    @Primary
    List<LeetCodeProblemRunner> leetCodeProblemRunners(
            @Qualifier("leetCodeBasicProblemRunners") List<LeetCodeProblemRunner> basicRunners,
            @Qualifier("leetCodeStructureProblemRunners") List<LeetCodeProblemRunner> structureRunners
    ) {
        List<LeetCodeProblemRunner> runners = new ArrayList<>(basicRunners.size() + structureRunners.size());
        runners.addAll(basicRunners);
        runners.addAll(structureRunners);
        return List.copyOf(runners);
    }

}
