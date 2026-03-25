package com.kuma.boot.oss.common.model.upload;

import java.io.Serializable;

public class UpLoadPartEntityTag implements Serializable {
   private static final long serialVersionUID = 2471854027355307627L;
   private int partNumber;
   private String eTag;

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.eTag == null ? 0 : this.eTag.hashCode());
      result = 31 * result + this.partNumber;
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         UpLoadPartEntityTag other = (UpLoadPartEntityTag)obj;
         if (this.eTag == null) {
            if (other.eTag != null) {
               return false;
            }
         } else if (!this.eTag.equals(other.eTag)) {
            return false;
         }

         return this.partNumber == other.partNumber;
      }
   }

   public String toString() {
      return "PartETag [partNumber=" + this.partNumber + ", eTag=" + this.eTag + "]";
   }

   public int getPartNumber() {
      return this.partNumber;
   }

   public void setPartNumber(int partNumber) {
      this.partNumber = partNumber;
   }

   public String getETag() {
      return this.eTag;
   }

   public void setETag(String eTag) {
      this.eTag = eTag;
   }
}
