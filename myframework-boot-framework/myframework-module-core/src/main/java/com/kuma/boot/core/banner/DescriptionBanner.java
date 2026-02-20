package com.kuma.boot.core.banner;


import com.taobao.text.ui.TableElement;
import com.taobao.text.util.RenderUtil;

import java.util.List;

/**
 * DescriptionBanner
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class DescriptionBanner {

    public String getBanner( List<Description> descriptions ) {
        TableElement table = new TableElement();
        for (Description description : descriptions) {
            table.leftCellPadding(description.getLeftCellPadding()).rightCellPadding(description.getRightCellPadding())
                    .row(description.getName(), description.getDescription());
        }

        return RenderUtil.render(table);
    }
}
