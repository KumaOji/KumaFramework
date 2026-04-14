package com.kuma.cloud.project4.retrofit;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 占位声明式 HTTP 客户端。
 * <p>
 * {@code retrofit-spring-boot-starter} 默认在启动类所在包（{@code com.kuma.cloud.project4}）下扫描
 * {@link RetrofitClient}；若该包内没有任何接口，会输出
 * “No RetrofitClient was found in '[com.kuma.cloud.project4]' package” 的 WARN。
 * </p>
 * <p>
 * 本接口仅用于消除该告警；默认 baseUrl 指向本机关闭端口，避免误连公网。接入真实 API 后可删除本接口或替换为业务定义。
 * </p>
 */
@RetrofitClient(baseUrl = "${kuma.cloud.project4.retrofit.placeholder-base-url:http://127.0.0.1:1/}")
public interface PlaceholderRetrofitClient {

    @GET("/")
    Call<ResponseBody> noop();
}
