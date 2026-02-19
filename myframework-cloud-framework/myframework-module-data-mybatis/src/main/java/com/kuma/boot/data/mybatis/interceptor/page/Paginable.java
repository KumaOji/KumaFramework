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

package com.kuma.boot.data.mybatis.interceptor.page;

public interface Paginable<T> {

    /** 总记录数 */
    int getTotalCount();

    /** 总页数 */
    int getTotalPage();

    /** 每页记录数 */
    int getPageSize();

    /** 当前页号 */
    int getPageNo();

    /** 是否第一页 */
    boolean isFirstPage();

    /** 是否最后一页 */
    boolean isLastPage();

    /** 返回下页的页号 */
    int getNextPage();

    /** 返回上页的页号 */
    int getPrePage();

    /** 取得当前页显示的项的起始序号 */
    int getBeginIndex();

    /** 取得当前页显示的末项序号 */
    int getEndIndex();

    /** 获取开始页*/
    int getBeginPage();

    /** 获取结束页*/
    int getEndPage();
}
