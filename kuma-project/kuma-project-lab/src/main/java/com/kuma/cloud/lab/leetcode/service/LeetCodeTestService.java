package com.kuma.cloud.lab.leetcode.service;

import com.kuma.cloud.lab.leetcode.domain.dto.LeetCodeRunDTO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeProblemVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeRunVO;
import com.kuma.cloud.lab.leetcode.domain.vo.LeetCodeScenarioVO;

import java.util.List;

public interface LeetCodeTestService {

    List<LeetCodeProblemVO> listProblems();

    LeetCodeProblemVO getProblem(int number);

    LeetCodeRunVO run(int number, LeetCodeRunDTO dto);

    LeetCodeScenarioVO runScenario();

    LeetCodeScenarioVO runProblemTests(int number);

}
