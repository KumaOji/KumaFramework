/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.taotaocloud.top/).
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

package com.kuma.boot.common.model.result;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;

/**
 * 返回实体类
 *
 * @author shuigedeng
 * @version 2023.04
 * @since 2023-05-10 15:50:14
 */
@Schema(description = "返回结果对象")
public class EmptyResult implements Serializable {

	@Serial
	private static final long serialVersionUID = -3685249101751401211L;

	public static EmptyResult empty() {
		return new EmptyResult();
	}

}
