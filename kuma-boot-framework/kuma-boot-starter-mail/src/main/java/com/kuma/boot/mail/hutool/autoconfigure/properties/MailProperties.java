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

package com.kuma.boot.mail.hutool.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedisLockProperties
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-07 21:15:27
 */
@ConfigurationProperties(prefix = MailProperties.PREFIX)
public class MailProperties {
   public static final String PREFIX = "kuma.boot.mail";

   private Boolean enabled;

   public Boolean getEnabled() {
      return enabled;
   }

   public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
   }

   /// **
   // * SMTP鏈嶅姟鍣ㄥ煙鍚?
   // */
   // @Value("")
   // private String host;
   //
   /// **
   // * SMTP鏈嶅姟绔彛
   // */
   // private Integer port;
   //
   /// **
   // * 鏄惁闇€瑕佺敤鎴峰悕瀵嗙爜楠岃瘉
   // */
   // private Boolean auth;
   //
   /// **
   // * 鐢ㄦ埛鍚?
   // */
   // private String user;
   //
   /// **
   // * 瀵嗙爜
   // */
   // private String pass;
   //
   /// **
   // * 鍙戦€佹柟锛岄伒寰猂FC-822鏍囧噯
   // */
   // private String from;
   //
   /// **
   // * 浣跨敤 STARTTLS瀹夊叏杩炴帴锛孲TARTTLS鏄绾枃鏈€氫俊鍗忚鐨勬墿灞曘€傚畠灏嗙函鏂囨湰杩炴帴鍗囩骇涓哄姞瀵嗚繛鎺ワ紙TLS鎴朣SL锛夛紝 鑰屼笉鏄娇鐢ㄤ竴涓崟鐙殑鍔犲瘑閫氫俊绔彛銆?
   // */
   // private Boolean starttlsEnable;
   //
   /// **
   // * 浣跨敤 SSL瀹夊叏杩炴帴
   // */
   // private Boolean sslEnable;
   //
   /// **
   // * SMTP瓒呮椂鏃堕暱锛屽崟浣嶆绉掞紝缂虹渷鍊间笉瓒呮椂
   // */
   // private Long timeout;
   //
   /// **
   // * Socket杩炴帴瓒呮椂鍊硷紝鍗曚綅姣锛岀己鐪佸€间笉瓒呮椂
   // */
   // private Long connectionTimeout;
   //
   // public boolean isEnabled() {
   //	return enabled;
   // }
   //
   // public void setEnabled(boolean enabled) {
   //	this.enabled = enabled;
   // }
   //
   // public String getHost() {
   //	return host;
   // }
   //
   // public void setHost(String host) {
   //	this.host = host;
   // }
   //
   // public Integer getPort() {
   //	return port;
   // }
   //
   // public void setPort(Integer port) {
   //	this.port = port;
   // }
   //
   // public Boolean getAuth() {
   //	return auth;
   // }
   //
   // public void setAuth(Boolean auth) {
   //	this.auth = auth;
   // }
   //
   // public String getUser() {
   //	return user;
   // }
   //
   // public void setUser(String user) {
   //	this.user = user;
   // }
   //
   // public String getPass() {
   //	return pass;
   // }
   //
   // public void setPass(String pass) {
   //	this.pass = pass;
   // }
   //
   // public String getFrom() {
   //	return from;
   // }
   //
   // public void setFrom(String from) {
   //	this.from = from;
   // }
   //
   // public Boolean getStarttlsEnable() {
   //	return starttlsEnable;
   // }
   //
   // public void setStarttlsEnable(Boolean starttlsEnable) {
   //	this.starttlsEnable = starttlsEnable;
   // }
   //
   // public Boolean getSslEnable() {
   //	return sslEnable;
   // }
   //
   // public void setSslEnable(Boolean sslEnable) {
   //	this.sslEnable = sslEnable;
   // }
   //
   // public Long getTimeout() {
   //	return timeout;
   // }
   //
   // public void setTimeout(Long timeout) {
   //	this.timeout = timeout;
   // }
   //
   // public Long getConnectionTimeout() {
   //	return connectionTimeout;
   // }
   //
   // public void setConnectionTimeout(Long connectionTimeout) {
   //	this.connectionTimeout = connectionTimeout;
   // }
}
