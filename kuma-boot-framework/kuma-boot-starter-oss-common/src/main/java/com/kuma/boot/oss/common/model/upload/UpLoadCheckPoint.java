package com.kuma.boot.oss.common.model.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.kuma.boot.oss.common.exception.OssException;
import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpLoadCheckPoint implements Serializable {
   private static final long serialVersionUID = 5424904565837227164L;
   public static final String UPLOAD_MAGIC = "FE8BB4EA-B593-4FAC-AD7A-2459A36E2E62";
   private String magic;
   private int md5;
   private String uploadFile;
   private UpLoadFileStat uploadFileStat;
   private String key;
   private String bucket;
   private String checkpointFile;
   private String uploadId;
   private List<UploadPart> uploadParts = Collections.synchronizedList(new ArrayList<>());
   private List<UpLoadPartEntityTag> partEntityTags = Collections.synchronizedList(new ArrayList<>());
   private long originPartSize;

   public synchronized void load(String checkpointFile) {
      try {
         UpLoadCheckPoint ucp = (UpLoadCheckPoint)JSONUtil.readJSONObject(new File(checkpointFile), StandardCharsets.UTF_8).toBean(this.getClass());
         this.assign(ucp);
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   public synchronized void dump() {
      this.setMd5(this.hashCode());

      try {
         FileUtil.writeUtf8String(JSONUtil.toJsonStr(this), this.checkpointFile);
      } catch (Exception e) {
         throw new OssException(e);
      }
   }

   public synchronized void update(int partIndex, UpLoadPartEntityTag partEntityTag, boolean completed) {
      this.getPartEntityTags().add(partEntityTag);
      ((UploadPart)this.getUploadParts().get(partIndex)).setCompleted(completed);
   }

   public synchronized boolean isValid() {
      if (this.getMagic() != null && this.getMagic().equals("FE8BB4EA-B593-4FAC-AD7A-2459A36E2E62") && this.getMd5() == this.hashCode()) {
         if (!FileUtil.exist(this.checkpointFile)) {
            return false;
         } else {
            File file = new File(this.uploadFile);
            return this.getUploadFileStat().getSize() == file.length() && this.getUploadFileStat().getLastModified() == file.lastModified();
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.key == null ? 0 : this.key.hashCode());
      result = 31 * result + (this.bucket == null ? 0 : this.bucket.hashCode());
      result = 31 * result + (this.checkpointFile == null ? 0 : this.checkpointFile.hashCode());
      result = 31 * result + (this.magic == null ? 0 : this.magic.hashCode());
      result = 31 * result + (this.partEntityTags == null ? 0 : this.partEntityTags.hashCode());
      result = 31 * result + (this.uploadFile == null ? 0 : this.uploadFile.hashCode());
      result = 31 * result + (this.uploadFileStat == null ? 0 : this.uploadFileStat.hashCode());
      result = 31 * result + (this.uploadId == null ? 0 : this.uploadId.hashCode());
      result = 31 * result + (this.uploadParts == null ? 0 : this.uploadParts.hashCode());
      result = 31 * result + (int)this.originPartSize;
      return result;
   }

   public void assign(UpLoadCheckPoint ucp) {
      this.setMagic(ucp.magic);
      this.setMd5(ucp.md5);
      this.setUploadFile(ucp.uploadFile);
      this.setUploadFileStat(ucp.uploadFileStat);
      this.setKey(ucp.key);
      this.setBucket(ucp.bucket);
      this.setCheckpointFile(ucp.checkpointFile);
      this.setUploadId(ucp.uploadId);
      this.setUploadParts(ucp.uploadParts);
      this.setPartEntityTags(ucp.partEntityTags);
      this.setOriginPartSize(ucp.originPartSize);
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

   public String getUploadFile() {
      return this.uploadFile;
   }

   public void setUploadFile(String uploadFile) {
      this.uploadFile = uploadFile;
   }

   public UpLoadFileStat getUploadFileStat() {
      return this.uploadFileStat;
   }

   public void setUploadFileStat(UpLoadFileStat uploadFileStat) {
      this.uploadFileStat = uploadFileStat;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getBucket() {
      return this.bucket;
   }

   public void setBucket(String bucket) {
      this.bucket = bucket;
   }

   public String getCheckpointFile() {
      return this.checkpointFile;
   }

   public void setCheckpointFile(String checkpointFile) {
      this.checkpointFile = checkpointFile;
   }

   public String getUploadId() {
      return this.uploadId;
   }

   public void setUploadId(String uploadId) {
      this.uploadId = uploadId;
   }

   public List getUploadParts() {
      return this.uploadParts;
   }

   public void setUploadParts(List uploadParts) {
      this.uploadParts = uploadParts;
   }

   public List getPartEntityTags() {
      return this.partEntityTags;
   }

   public void setPartEntityTags(List partEntityTags) {
      this.partEntityTags = partEntityTags;
   }

   public long getOriginPartSize() {
      return this.originPartSize;
   }

   public void setOriginPartSize(long originPartSize) {
      this.originPartSize = originPartSize;
   }
}
