package org.kyaru.pixiv.service.body.collect;

import org.kyaru.pixiv.service.body.collect.filterimpl.FilterBuilder;
import org.kyaru.pixiv.service.utils.network.RequestClient;
import org.kyaru.pixiv.service.utils.network.ReturnType;

import java.util.*;

public class Filter {
    public static FilterBuilder custom() {
        return new FilterBuilder();
    }

    private final Property<?> filterRules;

    public Filter(Property<?> filterRules) {
        this.filterRules = filterRules;
    }

    public List<String> filter(List<String> idList, RequestClient requestClient) {
        return idList.stream().filter(this.filterRules::isExpected).toList();
    }

    public interface Property<V> {
        boolean isExpected(String artworkHTML);

        static String getHTML(String artworkID, RequestClient requestClient) {
            return requestClient.download(artworkID, ReturnType.STRING);
        }
    }
}

