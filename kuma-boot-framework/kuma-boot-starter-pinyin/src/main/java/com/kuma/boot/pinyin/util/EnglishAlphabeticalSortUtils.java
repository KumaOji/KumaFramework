package com.kuma.boot.pinyin.util;

import com.kuma.boot.pinyin.bs.PinyinBs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EnglishAlphabeticalSortUtils {
   public static final String REG = "[a-zA-Z]*";

   public EnglishAlphabeticalSortUtils() {
   }

   public static List<BussinessMapAllDTO> englishAlphabeticalSort(List<BussinessAllListDTO> bussinessAllListDTOList) {
      List<BussinessMapAllDTO> getAllBussiness = new ArrayList();

      for(int i = 1; i <= 26; ++i) {
         String word = String.valueOf((char)(96 + i)).toUpperCase();
         List<BussinessAllListDTO> list = new ArrayList();

         for(BussinessAllListDTO str : bussinessAllListDTOList) {
            if (str.getBussinessName() != null) {
               String s = str.getBussinessName().substring(0, 1);
               char c;
               if (s.matches("[a-zA-Z]*")) {
                  c = s.charAt(0);
               } else {
                  c = ((String)PinyinBs.newInstance().toPinyinList(s.charAt(0)).get(0)).charAt(0);
               }

               String strs = String.valueOf(c);
               String se = strs.toUpperCase();
               if (word.equals(se)) {
                  list.add(str);
               }
            }
         }

         int nums = 0;
         if (list.size() > 0) {
            for(BussinessAllListDTO bussinessAllListDTO : list) {
               bussinessAllListDTOList.remove(bussinessAllListDTO);
            }

            BussinessMapAllDTO bussinessMapAllDTO = new BussinessMapAllDTO();
            bussinessMapAllDTO.setChildren(list);
            bussinessMapAllDTO.setIndex(word);
            int var10000 = nums + list.size();
            getAllBussiness.add(bussinessMapAllDTO);
         }
      }

      return getAllBussiness;
   }

   public static class BussinessAllListDTO implements Serializable {
      private String bussinessId;
      private Integer name;

      public BussinessAllListDTO() {
      }

      public String getBussinessName() {
         return this.bussinessId;
      }

      public void setBussinessName(String bussinessId) {
         this.bussinessId = bussinessId;
      }

      public Integer getName() {
         return this.name;
      }

      public void setName(Integer name) {
         this.name = name;
      }
   }

   public static class BussinessMapAllDTO implements Serializable {
      private List<BussinessAllListDTO> children;
      private String index;

      public BussinessMapAllDTO() {
      }

      public List<BussinessAllListDTO> getChildren() {
         return this.children;
      }

      public void setChildren(List<BussinessAllListDTO> children) {
         this.children = children;
      }

      public String getIndex() {
         return this.index;
      }

      public void setIndex(String index) {
         this.index = index;
      }
   }
}
