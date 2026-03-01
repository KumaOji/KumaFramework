/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.core.runtime.initializer;

import com.taobao.text.Color;
import com.taobao.text.ui.TableElement;
import com.taobao.text.util.RenderUtil;
import com.kuma.boot.core.banner.BannerConstants;
import com.kuma.boot.core.banner.Description;
import com.kuma.boot.core.banner.LogoBanner;
import com.kuma.boot.core.version.SpringCloudAlibabaVersion;
import com.kuma.boot.core.version.SpringCloudDependenciesVersion;
import com.kuma.boot.core.version.SpringCloudVersion;
import com.kuma.boot.common.support.version.KmcVersion;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kuma.boot.common.constant.CommonConstants.SPRING_BOOT_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_BACKEND;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_BACKEND_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_BLOG;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_BLOG_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_DATAV;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_DATAV_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_DEFAULT;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_DEFAULT_RESOURCE_LOCATION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_GITEE;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_GITEE_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_GITHUB;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_GITHUB_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_M;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_MANAGER;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_MANAGER_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_MERCHANT;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_MERCHANT_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_M_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_OPEN;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_OPEN_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_BANNER_URL;
import static com.kuma.boot.common.constant.CommonConstants.KMC_SPRING_BOOT_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_SPRING_CLOUD_ALIBABA_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_SPRING_CLOUD_DEPENDENCIES_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_SPRING_CLOUD_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_SPRING_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.KMC_VERSION;
import static com.kuma.boot.common.constant.CommonConstants.VERSION;

/**
 * Banner初始化
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 20:22:33
 */
@Order(2)
public class KmcBannerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        //LogUtils.started(KmcBannerInitializer.class, StarterNameConstants.CORE_STARTER);

        if (!(applicationContext instanceof AnnotationConfigApplicationContext)) {
            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            System.out.println();

            LogoBanner logoBanner = new LogoBanner(
                    KmcBannerInitializer.class,
                    KMC_BANNER_DEFAULT_RESOURCE_LOCATION,
                    KMC_BANNER_DEFAULT,
                    1,
                    6,
                    new Color[] {Color.red},
                    true);

            int leftCellPadding = 0;
            int rightCellPadding = 1;

            show(
                    logoBanner,
                    new Description(
                            KMC_VERSION,
                            KmcVersion.getVersion() == null
                                    ? environment.getProperty(VERSION, "")
                                    : KmcVersion.getVersion(),
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_SPRING_VERSION, SpringVersion.getVersion(), leftCellPadding, rightCellPadding),
                    new Description(
                            KMC_SPRING_BOOT_VERSION,
                            environment.getProperty(SPRING_BOOT_VERSION, SpringBootVersion.getVersion()),
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_SPRING_CLOUD_VERSION,
                            SpringCloudVersion.getVersion(),
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_SPRING_CLOUD_DEPENDENCIES_VERSION,
                            SpringCloudDependenciesVersion.getVersion(),
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_SPRING_CLOUD_ALIBABA_VERSION,
                            SpringCloudAlibabaVersion.getVersion(),
                            leftCellPadding,
                            rightCellPadding),
                    new Description("", "", leftCellPadding, rightCellPadding),
                    new Description(KMC_BANNER, KMC_BANNER_URL, leftCellPadding, rightCellPadding),
                    new Description(
                            KMC_BANNER_BLOG, KMC_BANNER_BLOG_URL, leftCellPadding, rightCellPadding),
                    new Description(
                            KMC_BANNER_M, KMC_BANNER_M_URL, leftCellPadding, rightCellPadding),
                    new Description(
                            KMC_BANNER_DATAV,
                            KMC_BANNER_DATAV_URL,
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_BANNER_MANAGER,
                            KMC_BANNER_MANAGER_URL,
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_BANNER_MERCHANT,
                            KMC_BANNER_MERCHANT_URL,
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_BANNER_OPEN, KMC_BANNER_OPEN_URL, leftCellPadding, rightCellPadding),
                    new Description(
                            KMC_BANNER_BACKEND,
                            KMC_BANNER_BACKEND_URL,
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_BANNER_GITEE,
                            KMC_BANNER_GITEE_URL,
                            leftCellPadding,
                            rightCellPadding),
                    new Description(
                            KMC_BANNER_GITHUB,
                            KMC_BANNER_GITHUB_URL,
                            leftCellPadding,
                            rightCellPadding));
        }
    }

    /**
     * show
     *
     * @param logoBanner logoBanner
     * @param descriptionList descriptionList
     * @since 2021-09-02 20:22:45
     */
    public void show(LogoBanner logoBanner, Description... descriptionList) {
        String bannerShown = System.getProperty(BannerConstants.BANNER_SHOWN, "true");
        if (!Boolean.parseBoolean(bannerShown)) {
            return;
        }

        String bannerShownAnsiMode = System.getProperty(BannerConstants.BANNER_SHOWN_ANSI_MODE, "true");
        if (Boolean.parseBoolean(bannerShownAnsiMode)) {
            System.out.println(logoBanner.getBanner());
        } else {
            System.out.println(logoBanner.getPlainBanner());
        }

        List<Description> descriptions = new ArrayList<>(Arrays.asList(descriptionList));

        TableElement table = new TableElement();
        for (Description description : descriptions) {
            table.leftCellPadding(description.getLeftCellPadding())
                    .rightCellPadding(description.getRightCellPadding())
                    .row(description.getName(), description.getDescription());
        }

        // DescriptionBanner descriptionBanner = new DescriptionBanner();
        System.out.println(RenderUtil.render(table, 100));
    }
}
