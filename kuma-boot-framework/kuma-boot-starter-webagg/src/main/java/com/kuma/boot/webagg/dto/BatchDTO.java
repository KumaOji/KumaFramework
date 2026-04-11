package com.kuma.boot.webagg.dto;

import com.kuma.boot.web.validation.annotation.StringEnums;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Schema(
   description = "\u6279\u91cf\u64cd\u4f5cDTO"
)
public record BatchDTO<SaveDTO, UpdateDTO, I extends Serializable>(@NotBlank(
   message = "\u8bf7\u6c42\u65b9\u5f0f\u4e0d\u80fd\u4e3a\u7a7a"
) String method, List<SaveDTO> batchCreate, List<BatchUpdate<UpdateDTO, I>> batchUpdate, List<I> batchDelete) implements Serializable {
   private static final long serialVersionUID = -4194344880194881367L;

   public BatchDTO(@Schema(description = "\u8bf7\u6c42\u65b9\u5f0f",allowableValues = {"create", "update", "delete"},requiredMode = RequiredMode.REQUIRED) @StringEnums(enumList = {"create", "update", "delete"},message = "\u8bf7\u6c42\u65b9\u5f0f\u9519\u8bef") @NotBlank(
   message = "\u8bf7\u6c42\u65b9\u5f0f\u4e0d\u80fd\u4e3a\u7a7a"
) String method, @Schema(description = "\u6279\u91cf\u6dfb\u52a0\u6570\u636eDTO") List<SaveDTO> batchCreate, @Schema(description = "\u6279\u91cf\u66f4\u65b0\u6570\u636eDTO") List<BatchUpdate<UpdateDTO, I>> batchUpdate, @Schema(description = "\u6279\u91cf\u5220\u9664\u6570\u636eDTO") List<I> batchDelete) {
      this.method = method;
      this.batchCreate = batchCreate;
      this.batchUpdate = batchUpdate;
      this.batchDelete = batchDelete;
   }

   @Schema(
      description = "\u8bf7\u6c42\u65b9\u5f0f",
      allowableValues = {"create", "update", "delete"},
      requiredMode = RequiredMode.REQUIRED
   )
   @StringEnums(
      enumList = {"create", "update", "delete"},
      message = "\u8bf7\u6c42\u65b9\u5f0f\u9519\u8bef"
   )
   public @NotBlank(
   message = "\u8bf7\u6c42\u65b9\u5f0f\u4e0d\u80fd\u4e3a\u7a7a"
) String method() {
      return this.method;
   }

   @Schema(
      description = "\u6279\u91cf\u6dfb\u52a0\u6570\u636eDTO"
   )
   public List<SaveDTO> batchCreate() {
      return this.batchCreate;
   }

   @Schema(
      description = "\u6279\u91cf\u66f4\u65b0\u6570\u636eDTO"
   )
   public List<BatchUpdate<UpdateDTO, I>> batchUpdate() {
      return this.batchUpdate;
   }

   @Schema(
      description = "\u6279\u91cf\u5220\u9664\u6570\u636eDTO"
   )
   public List<I> batchDelete() {
      return this.batchDelete;
   }

   @Schema(
      description = "\u6279\u91cf\u66f4\u65b0\u64cd\u4f5cDTO"
   )
   public static record BatchUpdate<UpdateDTO, I extends Serializable>(I id, UpdateDTO updateDTO) {
      public BatchUpdate(@Schema(description = "id") I id, @Schema(description = "\u66f4\u65b0\u6570\u636e\u5bf9\u8c61") UpdateDTO updateDTO) {
         this.id = id;
         this.updateDTO = updateDTO;
      }

      @Schema(
         description = "id"
      )
      public I id() {
         return this.id;
      }

      @Schema(
         description = "\u66f4\u65b0\u6570\u636e\u5bf9\u8c61"
      )
      public UpdateDTO updateDTO() {
         return this.updateDTO;
      }
   }
}
