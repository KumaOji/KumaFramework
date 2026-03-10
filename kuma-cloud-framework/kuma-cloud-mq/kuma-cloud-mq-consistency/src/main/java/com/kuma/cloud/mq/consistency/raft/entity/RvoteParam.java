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

package com.kuma.cloud.mq.consistency.raft.entity;

import com.kuma.cloud.mq.consistency.raft.Consensus;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 请求投票 RPC 参数.
 *
 * @author kuma
 * @see Consensus#requestVote(RvoteParam)
 */
@Getter
@Setter
@Builder
@Data
public class RvoteParam implements Serializable {
    /** 候选人的任期号  */
    private long term;

    /** 被请求者 ID(ip:selfPort) */
    private String serverId;

    /** 请求选票的候选人的 Id(ip:selfPort) */
    private String candidateId;

    /** 候选人的最后日志条目的索引值 */
    private long lastLogIndex;

    /** 候选人最后日志条目的任期号  */
    private long lastLogTerm;
}
