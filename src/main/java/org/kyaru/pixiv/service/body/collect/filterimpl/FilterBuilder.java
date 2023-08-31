package org.kyaru.pixiv.service.body.collect.filterimpl;

import org.kyaru.pixiv.service.body.collect.Filter;
import org.kyaru.pixiv.service.body.process.TaskID;

import java.util.ArrayList;
import java.util.List;

public class FilterBuilder {
    private Filter.Property<?> filterRules;

    public Filter build() {
        return new Filter(this.filterRules);
    }

    public FilterBuilder setExpectLikes(int likeCount) {
        return set(new FilterProperty.Likes(likeCount));
    }

    public FilterBuilder setExpectBookmarks(int bookmarkCount) {
        return set(new FilterProperty.Bookmarks(bookmarkCount));
    }

    public FilterBuilder setExpectViews(int viewCount) {
        return set(new FilterProperty.Views(viewCount));
    }

    public FilterBuilder setExpectTags(List<String> tags) {
        return set(new FilterProperty.Tags(tags, FilterProperty.Tags.Type.WHITELIST));
    }

    public FilterBuilder setUnExpectTags(List<String> tags) {
        return set(new FilterProperty.Tags(tags, FilterProperty.Tags.Type.BLACKLIST));
    }

    public FilterBuilder setDateAfter(java.util.Date date) {
        return set(new FilterProperty.Date(date, FilterProperty.Date.Type.AFTER));
    }

    public FilterBuilder setDateBefore(java.util.Date date) {
        return set(new FilterProperty.Date(date, FilterProperty.Date.Type.BEFORE));
    }

    public FilterBuilder setExpectLabel(TaskID.Label label) {
        return set(new FilterProperty.Label(label));
    }

    private FilterBuilder set(Filter.Property<?> property) {
        this.filterRules = combine(this.filterRules, property);
        return this;
    }

    private static Filter.Property<?> combine(Filter.Property<?> property1, Filter.Property<?> property2) {
        return artworkHTML -> property1.isExpected(artworkHTML) && property2.isExpected(artworkHTML);
    }
}
