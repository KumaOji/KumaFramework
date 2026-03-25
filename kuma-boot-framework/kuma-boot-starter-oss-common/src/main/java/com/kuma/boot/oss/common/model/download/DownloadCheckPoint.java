package com.kuma.boot.oss.common.model.download;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadCheckPoint implements Serializable {
   private static final long serialVersionUID = 4682293344365787077L;
   public static final String DOWNLOAD_MAGIC = "92611BED-89E2-46B6-89E5-72F273D4B0A3";
   private String magic;
   private int md5;
   private String downloadFile;
   private String bucketName;
   private String key;
   private String checkPointFile;
   private DownloadObjectStat objectStat;
   private List downloadParts = Collections.synchronizedList(new ArrayList());
   private long originPartSize;

   public synchronized void load(String checkPointFile) throws IOException, ClassNotFoundException {
      InputStream inputStream = FileUtil.getInputStream(checkPointFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
      DownloadCheckPoint dcp = (DownloadCheckPoint)objectInputStream.readObject();
      this.assign(dcp);
      IoUtil.close(objectInputStream);
      IoUtil.close(inputStream);
   }

   public synchronized void dump() throws IOException {
      this.md5 = this.hashCode();
      FileOutputStream outputStream = new FileOutputStream(this.checkPointFile);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
      objectOutputStream.writeObject(this);
      IoUtil.close(objectOutputStream);
      IoUtil.close(outputStream);
   }

   public String getTempDownloadFile() {
      return this.downloadFile + ".tmp";
   }

   public synchronized void update(int index, boolean completed) {
      ((DownloadPart)this.downloadParts.get(index)).setCompleted(completed);
   }

   public synchronized boolean isValid(DownloadObjectStat objectStat) {
      if (this.magic != null && this.magic.equals("92611BED-89E2-46B6-89E5-72F273D4B0A3") && this.md5 == this.hashCode()) {
         return this.objectStat.getSize() == objectStat.getSize() && this.objectStat.getLastModified().equals(objectStat.getLastModified()) && this.objectStat.getDigest().equals(objectStat.getDigest());
      } else {
         return false;
      }
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.bucketName == null ? 0 : this.bucketName.hashCode());
      result = 31 * result + (this.downloadFile == null ? 0 : this.downloadFile.hashCode());
      result = 31 * result + (this.checkPointFile == null ? 0 : this.checkPointFile.hashCode());
      result = 31 * result + (this.magic == null ? 0 : this.magic.hashCode());
      result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
      result = 31 * result + (this.objectStat == null ? 0 : this.objectStat.hashCode());
      result = 31 * result + (this.downloadParts == null ? 0 : this.downloadParts.hashCode());
      return result;
   }

   private void assign(DownloadCheckPoint dcp) {
      this.setMagic(dcp.getMagic());
      this.setMd5(dcp.getMd5());
      this.setDownloadFile(dcp.getDownloadFile());
      this.setCheckPointFile(dcp.getCheckPointFile());
      this.setBucketName(dcp.getBucketName());
      this.setKey(dcp.getKey());
      this.setObjectStat(dcp.getObjectStat());
      this.setDownloadParts(dcp.getDownloadParts());
      this.setOriginPartSize(dcp.getOriginPartSize());
   }

   public String getMagic() {
      return this.magic;
   }

   public void setMagic(String magic) {
      this.magic = magic;
   }

   public int getMd5() {
      return this.md5;
   }

   public void setMd5(int md5) {
      this.md5 = md5;
   }

   public String getDownloadFile() {
      return this.downloadFile;
   }

   public void setDownloadFile(String downloadFile) {
      this.downloadFile = downloadFile;
   }

   public String getBucketName() {
      return this.bucketName;
   }

   public void setBucketName(String bucketName) {
      this.bucketName = bucketName;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getCheckPointFile() {
      return this.checkPointFile;
   }

   public void setCheckPointFile(String checkPointFile) {
      this.checkPointFile = checkPointFile;
   }

   public DownloadObjectStat getObjectStat() {
      return this.objectStat;
   }

   public void setObjectStat(DownloadObjectStat objectStat) {
      this.objectStat = objectStat;
   }

   public List getDownloadParts() {
      return this.downloadParts;
   }

   public void setDownloadParts(List downloadParts) {
      this.downloadParts = downloadParts;
   }

   public long getOriginPartSize() {
      return this.originPartSize;
   }

   public void setOriginPartSize(long originPartSize) {
      this.originPartSize = originPartSize;
   }
}
