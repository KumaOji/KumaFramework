package com.kuma.boot.idgenerator.uid1;

import com.kuma.boot.idgenerator.uid1.exception.UidGenerateException;

public interface UidGenerator {
   long getUID() throws UidGenerateException;

   String parseUID(long uid);
}
