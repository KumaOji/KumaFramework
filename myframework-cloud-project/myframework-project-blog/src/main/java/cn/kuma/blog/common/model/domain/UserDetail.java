package cn.kuma.blog.common.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetail {

    private String note;

    private String unitName;

    private String ip;

    private String userName;

    private String userID;

    private String timeOut;

    @JsonProperty("hwnodeno")
    private String hwNodeNo;

    private String phone;

    private Integer identity;

    @JsonProperty("ssysfrom")
    private String sSysFrom;

    private boolean adminGrade;

    @JsonProperty("unitid")
    private String unitId;

    private String info;

}
