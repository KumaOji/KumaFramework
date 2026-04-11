package com.kuma.boot.canal.canalquick.parser;

import com.alibaba.fastjson2.JSONObject;
import com.kuma.boot.canal.canalquick.event.EventInfo;

public interface RowDataCustomParser {
   JSONObject parse(EventInfo eventInfo);
}
