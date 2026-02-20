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

package com.kuma.boot.office.easyexcel.easyexcelcell;

import com.alibaba.excel.write.style.row.AbstractRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.Row;

/** EasyExcel解析动态表头及导出 设置表头的自动调整行高策略 */
public class CellRowHeightStyleStrategy extends AbstractRowHeightStyleStrategy {

    @Override
    protected void setHeadColumnHeight(Row row, int relativeRowIndex) {
        // 设置主标题行高为17.7
        if (relativeRowIndex == 0) {
            // 如果excel需要显示行高为15，那这里就要设置为15*20=300
            row.setHeight((short) 3240);
        }
    }

    @Override
    protected void setContentColumnHeight(Row row, int relativeRowIndex) {}
}
