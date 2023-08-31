package org.kyaru.pixiv.service.config;

import org.json.simple.JSONObject;
import org.jsoup.internal.StringUtil;
import org.kyaru.pixiv.service.body.collect.Filter;
import org.kyaru.pixiv.service.body.collect.filterimpl.FilterBuilder;
import org.kyaru.pixiv.service.body.process.TaskID;
import org.kyaru.pixiv.service.utils.file.FileOperator;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public record FilterConfig(Path settingFile) {
    public Filter getFilter() {
        JSONObject json = new FileOperator(settingFile).read();
        FilterBuilder filterBuilder = Filter.custom();
        if (json.containsKey(JSON_KEY.VIEWS.path))
            filterBuilder.setExpectViews(Integer.parseInt(json.get(JSON_KEY.VIEWS.path).toString()));
        if (json.containsKey(JSON_KEY.LIKES.path))
            filterBuilder.setExpectLikes(Integer.parseInt(json.get(JSON_KEY.LIKES.path).toString()));
        if (json.containsKey(JSON_KEY.BOOKMARKS.path))
            filterBuilder.setExpectBookmarks(Integer.parseInt(json.get(JSON_KEY.BOOKMARKS.path).toString()));
        if (json.containsKey(JSON_KEY.AFTER_DATE.path))
            filterBuilder.setDateAfter(new Date(Long.parseLong(json.get(JSON_KEY.AFTER_DATE.path).toString())));
        if (json.containsKey(JSON_KEY.BEFORE_DATE.path))
            filterBuilder.setDateBefore(new Date(Long.parseLong(json.get(JSON_KEY.BEFORE_DATE.path).toString())));
        if (json.containsKey(JSON_KEY.TAG_WHITELIST.path))
            filterBuilder.setExpectTags(List.of(json.get(JSON_KEY.TAG_WHITELIST.path).toString().split(",")));
        if (json.containsKey(JSON_KEY.VIEWS.path))
            filterBuilder.setUnExpectTags(List.of(json.get(JSON_KEY.TAG_WHITELIST.path).toString().split(",")));
        if (json.containsKey(JSON_KEY.LABEL.path))
            filterBuilder.setExpectLabel(TaskID.Label.parseLabel(json.get(JSON_KEY.LABEL.path).toString()));
        return filterBuilder.build();
    }

    public static Builder custom(Path settingFile) {
        return new Builder(settingFile);
    }

    public static class Builder {
        private final FileOperator file;
        private JSONObject jsonObject ;

        public Builder(Path settingFile) {
            this.file = new FileOperator(settingFile);
            this.jsonObject = file.read();
        }

        public Builder setExpectViews(int count) {
            return set(JSON_KEY.VIEWS, Integer.toString(count));
        }

        public Builder setExpectLikes(int count) {
            return set(JSON_KEY.LIKES, Integer.toString(count));
        }

        public Builder setExpectBookmarks(int count) {
            return set(JSON_KEY.BOOKMARKS, Integer.toString(count));
        }

        public Builder setDateAfter(String date) {
            try {
                return set(JSON_KEY.AFTER_DATE, String.valueOf(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
            } catch (ParseException e) {
                throw new RuntimeException("wrong format of date, should be yyyy-MM-dd");
            }
        }

        public Builder setDateBefore(String date) {
            try {
                return set(JSON_KEY.BEFORE_DATE, String.valueOf(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
            } catch (ParseException e) {
                throw new RuntimeException("wrong format of date, should be yyyy-MM-dd");
            }
        }

        public Builder setWhitelistTag(List<String> tags) {
            return set(JSON_KEY.TAG_WHITELIST, StringUtil.join(tags, ","));
        }

        public Builder setBlacklistTag(List<String> tags) {
            return set(JSON_KEY.TAG_BLACKLIST, StringUtil.join(tags, ","));
        }

        public Builder setExpectLabel(TaskID.Label.Tag tag, TaskID.Label.Format format) {
            return set(JSON_KEY.LABEL, new TaskID.Label(tag, format).toString());
        }

        public Path confirm() {
            this.file.write(this.jsonObject);
            return this.file.filePath();
        }

        private Builder set(JSON_KEY key, String value) {
            jsonObject.put(key.path, value);
            return this;
        }
    }

    private enum JSON_KEY {
        VIEWS("views"),
        LIKES("likes"),
        BOOKMARKS("bookmarks"),
        AFTER_DATE("after_date"),
        BEFORE_DATE("before_date"),
        TAG_WHITELIST("tag_whitelist"),
        TAG_BLACKLIST("tag_blacklist"),
        LABEL("label");
        private final String path;

        JSON_KEY(String path) {
            this.path = path;
        }
    }
}
