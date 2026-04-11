package com.kuma.boot.useragent.utils;

import com.alibaba.fastjson2.JSONObject;
import com.google.i18n.phonenumbers.PhoneNumberToCarrierMapper;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import java.util.Locale;

public class PhoneToRegionUtil {
   private static final PhoneNumberUtil PHONE_NUMBER_UTIL = PhoneNumberUtil.getInstance();
   private static final PhoneNumberToCarrierMapper CARRIER_MAPPER = PhoneNumberToCarrierMapper.getInstance();
   private static final PhoneNumberOfflineGeocoder GEO_CODER = PhoneNumberOfflineGeocoder.getInstance();

   public PhoneToRegionUtil() {
   }

   public static boolean isValidNumber(String phone) {
      return PHONE_NUMBER_UTIL.isValidNumber(getPhoneNumber(phone));
   }

   public static String getPhoneCarrier(String phone) {
      return isValidNumber(phone) ? CARRIER_MAPPER.getNameForNumber(getPhoneNumber(phone), Locale.CHINA) : "";
   }

   public static String getRegionInfoByPhone(String phone) {
      return isValidNumber(phone) ? GEO_CODER.getDescriptionForNumber(getPhoneNumber(phone), Locale.CHINESE) : "";
   }

   private static Phonenumber.PhoneNumber getPhoneNumber(String phone) {
      Phonenumber.PhoneNumber phoneNumber = new Phonenumber.PhoneNumber();
      phoneNumber.setCountryCode(86);
      phoneNumber.setNationalNumber(Long.parseLong(phone));
      return phoneNumber;
   }

   public static JSONObject getPhoneAffiliationInfo(String phone) {
      JSONObject affiliation = new JSONObject();
      affiliation.put("phone", phone);
      affiliation.put("carrier", getPhoneCarrier(phone));
      affiliation.put("region", getRegionInfoByPhone(phone));
      return affiliation;
   }
}
