package org.kyaru.pixiv.service.download.collect;

import org.kyaru.pixiv.service.download.collect.filterimpl.FilterBuilder;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;

import java.util.*;

public class Filter {
    public static FilterBuilder custom() {
        return new FilterBuilder();
    }

    private final List<Property<?>> filterRules;

    public Filter(List<Property<?>> filterRules) {
        this.filterRules = filterRules;
    }

    public boolean isExpect(String artworkID, RequestClient requestClient) {
        String html = Property.getHTML(artworkID, requestClient);
        for (Property<?> filterRule : this.filterRules) {
            if (!filterRule.isExpected(html)) {
                return false;
            }
        }
        return true;
    }

    public interface Property<T> {
        boolean isExpected(String artworkHTML);

        static String getHTML(String artworkID, RequestClient requestClient) {
            return requestClient.download(artworkID, ReturnType.STRING);
        }
    }
}

