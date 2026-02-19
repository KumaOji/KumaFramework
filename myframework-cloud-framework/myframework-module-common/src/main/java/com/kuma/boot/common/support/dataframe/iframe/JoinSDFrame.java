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

package com.kuma.boot.common.support.dataframe.iframe;

import com.kuma.boot.common.support.dataframe.iframe.support.DefaultJoin;
import com.kuma.boot.common.support.dataframe.iframe.support.Join;
import com.kuma.boot.common.support.dataframe.iframe.support.JoinOn;
import com.kuma.boot.common.support.dataframe.iframe.support.VoidJoin;

/**
 * @author caizhihao
 * @param <T>
 */
public interface JoinSDFrame<T> extends JoinIFrame<T> {

    /**
     * inner join Frame
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> join(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * inner join Frame If successfully associated with other Frame record, it will only
     * be associated once
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> joinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * inner join Frame such as {@link IFrame#join(IFrame, JoinOn, Join)}, but the default
     * Join is {@link DefaultJoin}, it will automatically map to a new Frame based on the
     * same name
     * @param other other frame
     * @param on connection conditions
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> join(IFrame<K> other, JoinOn<T, K> on);

    /**
     * just Execute inner join operation， will not change the data of the frame
     * @param other other frame
     * @param on connection conditions
     * @param <K> other Frame type
     */
    <K> SDFrame<T> joinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);

    /**
     * just Execute inner join operation， will not change the data of the frame If
     * successfully associated with other Frame record, it will only be associated once
     * @param other other frame
     * @param on connection conditions
     * @param <K> other Frame type
     */
    <K> SDFrame<T> joinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);

    /**
     * left join Frame if connection conditions false, The callback value K for Join will
     * be null， always keep T
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> leftJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * left join Frame if connection conditions false, The callback value K for Join will
     * be null， always keep T If successfully associated with other Frame record, it will
     * only be associated once
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> leftJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * left join Frame such as {@link IFrame#leftJoin(IFrame, JoinOn, Join)}, but the
     * default Join is {@link DefaultJoin},
     * @param other other frame
     * @param on connection conditions
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> leftJoin(IFrame<K> other, JoinOn<T, K> on);

    /**
     * just Execute left join operation， will not change the data of the frame if
     * connection conditions false, The callback value K for Join will be null， always
     * keep T
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <K> other Frame type
     */
    <K> SDFrame<T> leftJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);

    /**
     * just Execute left join operation， will not change the data of the frame if
     * connection conditions false, The callback value K for Join will be null， always
     * keep T If successfully associated with other Frame record, it will only be
     * associated once
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <K> other Frame type
     */
    <K> SDFrame<T> leftJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);

    /**
     * right join Frame if connection conditions false, The callback value T for Join will
     * be null， always keep K
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> rightJoin(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * right join Frame if connection conditions false, The callback value T for Join will
     * be null， always keep K If successfully associated with other Frame record, it will
     * only be associated once
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> rightJoinOnce(IFrame<K> other, JoinOn<T, K> on, Join<T, K, R> join);

    /**
     * right join Frame such as {@link IFrame#rightJoin(IFrame, JoinOn, Join)}, but the
     * default Join is {@link DefaultJoin},
     * @param other other frame
     * @param on connection conditions
     * @param <R> new Frame type
     * @param <K> other Frame type
     */
    <R, K> SDFrame<R> rightJoin(IFrame<K> other, JoinOn<T, K> on);

    /**
     * just Execute right join operation， will not change the data of the frame if
     * connection conditions false, The callback value T for Join will be null， always
     * keep K
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <K> other Frame type
     */
    <K> SDFrame<T> rightJoinVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);

    /**
     * just Execute right join operation， will not change the data of the frame if
     * connection conditions false, The callback value T for Join will be null， always
     * keep K If successfully associated with other Frame record, it will only be
     * associated once
     * @param other other frame
     * @param on connection conditions
     * @param join Connection logic
     * @param <K> other Frame type
     */
    <K> SDFrame<T> rightJoinOnceVoid(IFrame<K> other, JoinOn<T, K> on, VoidJoin<T, K> join);
}
