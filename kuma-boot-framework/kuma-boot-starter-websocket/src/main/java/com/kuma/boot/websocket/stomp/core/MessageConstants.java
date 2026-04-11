package com.kuma.boot.websocket.stomp.core;

import com.kuma.boot.common.constant.SymbolConstants;

public interface MessageConstants {
   String PROPERTY_PREFIX_SPRING = "spring";
   String PROPERTY_PREFIX_Websocket = "Websocket";
   String AREA_PREFIX = "data:";
   String BEARER_TYPE = "Bearer";
   String BEARER_TOKEN = "Bearer" + SymbolConstants.SPACE;
   String AVATAR = "avatar";
   String EMPLOYEE_ID = "employeeId";
   String OPEN_ID = "openid";
   String PRINCIPAL = "principal";
   String ROLES = "roles";
   String USERNAME = "username";
   String X_Websocket_OPEN_ID = "X-Websocket-Open-Id";
   String MSG_AREA_PREFIX = "data:msg:";
   String REDIS_CURRENT_ONLINE_USER = "data:msg:online:user";
   String PROPERTY_PREFIX_MESSAGE = "Websocket.message";
   String PROPERTY_PREFIX_WEBSOCKET = "Websocket.message.websocket";
   String WEBSOCKET_CHANNEL_PROXY_BROADCAST = "/broadcast";
   String WEBSOCKET_CHANNEL_PROXY_PERSONAL = "/personal";
   String WEBSOCKET_DESTINATION_BROADCAST_NOTICE = "/broadcast/notice";
   String WEBSOCKET_DESTINATION_BROADCAST_ONLINE = "/broadcast/online";
   String WEBSOCKET_DESTINATION_PERSONAL_MESSAGE = "/personal/message";
}
