package cn.kuma.blog.framework.util;

import cn.kuma.blog.common.model.domain.UserDetail;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDetailUtils {

    public static final String USER_DETAIL = "userDetail";

    public static UserDetail getUserDetail() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .map(attributes -> attributes.getAttribute(USER_DETAIL, RequestAttributes.SCOPE_REQUEST))
            .map(userDetail -> (UserDetail) userDetail)
            .orElse(null);
    }

    /**
     * @return 返回所有管理员用户
     */
    public static List<UserDetail> getAllAdminUserDetail() {
        return new ArrayList<>();
    }

}
