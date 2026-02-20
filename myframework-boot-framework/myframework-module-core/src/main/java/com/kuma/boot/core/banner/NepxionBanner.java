package com.kuma.boot.core.banner;


import java.util.ArrayList;
import java.util.List;

/**
 * NepxionBanner
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class NepxionBanner {

    public static void show( LogoBanner logoBanner, Description... descriptionList ) {
        String bannerShown = System.getProperty(BannerConstants.BANNER_SHOWN, "true");
        if (!Boolean.parseBoolean(bannerShown)) {
            return;
        }

        System.out.println("");
        String bannerShownAnsiMode = System.getProperty(BannerConstants.BANNER_SHOWN_ANSI_MODE, "false");
        if (Boolean.parseBoolean(bannerShownAnsiMode)) {
            System.out.println(logoBanner.getBanner());
        } else {
            System.out.println(logoBanner.getPlainBanner());
        }

        List<Description> descriptions = new ArrayList<Description>();
        for (Description description : descriptionList) {
            descriptions.add(description);
        }
        descriptions.add(new Description(BannerConstants.SITE + ":", BannerConstants.NEPXION_SITE, 0, 1));

        DescriptionBanner descriptionBanner = new DescriptionBanner();
        System.out.println(descriptionBanner.getBanner(descriptions));
    }
}
