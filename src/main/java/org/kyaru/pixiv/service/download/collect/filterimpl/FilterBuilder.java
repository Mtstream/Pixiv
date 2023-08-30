package org.kyaru.pixiv.service.download.collect.filterimpl;

import org.kyaru.pixiv.service.download.collect.Filter;
import org.kyaru.pixiv.service.download.process.TaskID;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {
    private final List<Filter.Property<?>> filterRules = new ArrayList<>();

    public Filter build() {
        return new Filter(this.filterRules);
    }

    public FilterBuilder setExpectLikes(int likeCount) {
        return set(new FilterPropertyImpl.Likes(likeCount));
    }

    public FilterBuilder setExpectBookmarks(int bookmarkCount) {
        return set(new FilterPropertyImpl.Bookmarks(bookmarkCount));
    }

    public FilterBuilder setExpectViews(int viewCount) {
        return set(new FilterPropertyImpl.Views(viewCount));
    }

    public FilterBuilder setExpectTags(List<String> tags) {
        return set(new FilterPropertyImpl.Tags(tags, FilterPropertyImpl.Tags.Type.WHITELIST));
    }

    public FilterBuilder setUnExpectTags(List<String> tags) {
        return set(new FilterPropertyImpl.Tags(tags, FilterPropertyImpl.Tags.Type.BLACKLIST));
    }

    public FilterBuilder setAfterDate(java.util.Date date) {
        return set(new FilterPropertyImpl.Date(date, FilterPropertyImpl.Date.Type.AFTER));
    }

    public FilterBuilder setBeforeDate(java.util.Date date) {
        return set(new FilterPropertyImpl.Date(date, FilterPropertyImpl.Date.Type.BEFORE));
    }

    public FilterBuilder setExpectLabel(TaskID.Label label) {
        return set(new FilterPropertyImpl.Label(label));
    }

    private <T> FilterBuilder set(Filter.Property<T> property) {
        this.filterRules.add(property);
        return this;
    }
}
