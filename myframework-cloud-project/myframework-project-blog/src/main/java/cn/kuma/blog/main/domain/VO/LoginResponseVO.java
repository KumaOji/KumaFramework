package cn.kuma.blog.main.domain.VO;

import cn.kuma.blog.common.model.domain.UserDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "登录响应")
@Data
public class LoginResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token
     */
    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserDetail userDetail;
}
