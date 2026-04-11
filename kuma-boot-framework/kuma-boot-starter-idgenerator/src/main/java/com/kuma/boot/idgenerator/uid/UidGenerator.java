package com.kuma.boot.idgenerator.uid;

import com.kuma.boot.idgenerator.uid.exception.UidGenerateException;
import java.util.Map;

public interface UidGenerator {
   long getUID() throws UidGenerateException;

   String parseUID(long uid);

   Map<String, Object> parseUid(long uid);
}
