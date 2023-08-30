package org.kyaru.pixiv.service.download.collect.filterimpl;

import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.download.collect.Filter;
import org.kyaru.pixiv.service.download.process.TaskID;
import org.seimicrawler.xpath.JXDocument;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record FilterPropertyImpl() {
    public record Label(TaskID.Label expectValue) implements Filter.Property<TaskID.Label> {
        private final static String GIF_TAG_XPATH = "//head/title/text()";
        private final static String R18_TAG_XPATH = "//head/meta[@property=\"twitter:title\"]/@content";

        @Override
        public boolean isExpected(String artworkHTML) {
            return expectValue.equals(getActualValue(artworkHTML));
        }

        public static TaskID.Label getActualValue(String artworkHTML) {
            JXDocument jxDoc = new JXDocument(Jsoup.parse(artworkHTML).getAllElements());
            return new TaskID.Label(
                    jxDoc.selNOne(R18_TAG_XPATH).asString().contains("R-18") ? TaskID.Label.Tag.R18 : TaskID.Label.Tag.NOR,
                    jxDoc.selNOne(GIF_TAG_XPATH).asString().contains("动图") ? TaskID.Label.Format.GIF : TaskID.Label.Format.IMG
            );
        }
    }

    record Views(Integer expectValue) implements Filter.Property<Integer> {
        @Override
        public boolean isExpected(String artworkHTML) {
            return getActualValue(artworkHTML) > expectValue;
        }

        public static Integer getActualValue(String artworkHTML) {
            Matcher matcher = Pattern.compile("\"viewCount\":(\\d+),").matcher(artworkHTML);
            if (matcher.find()) return Integer.parseInt(matcher.group(1));
            return 0;
        }
    }

    record Likes(Integer expectValue) implements Filter.Property<Integer> {
        @Override
        public boolean isExpected(String artworkHTML) {
            return getActualValue(artworkHTML) > expectValue;
        }

        public static Integer getActualValue(String artworkHTML) {
            Matcher matcher = Pattern.compile("\"likeCount\":(\\d+),").matcher(artworkHTML);
            if (matcher.find()) return Integer.parseInt(matcher.group(1));
            return 0;
        }
    }

    record Bookmarks(Integer expectValue) implements Filter.Property<Integer> {
        @Override
        public boolean isExpected(String artworkHTML) {
            return getActualValue(artworkHTML) > expectValue;
        }

        public static Integer getActualValue(String artworkHTML) {
            Matcher matcher = Pattern.compile("\"bookmarkCount\":(\\d+),").matcher(artworkHTML);
            if (matcher.find()) return Integer.parseInt(matcher.group(1));
            return 0;
        }
    }

    record Date(java.util.Date expectValue, Type type) implements Filter.Property<java.util.Date> {
        enum Type {AFTER, BEFORE;}
        @Override
        public boolean isExpected(String artworkHTML) {
            try {
                int dis = getActualValue(artworkHTML).compareTo(expectValue);
                return switch (type) {
                    case AFTER -> dis >= 0;
                    case BEFORE -> dis <= 0;
                };
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        public static java.util.Date getActualValue(String artworkHTML) throws ParseException {
            Matcher matcher = Pattern.compile("\"createDate\":\"(.*?)\"},").matcher(artworkHTML);
            if (matcher.find()) return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(matcher.group(1));
            return null;
        }
    }

    record Tags(List<String> expectValue, Type type) implements Filter.Property<List<String>> {
        public enum Type {BLACKLIST, WHITELIST}
        @Override
        public boolean isExpected(String artworkHTML) {
            return switch (type) {
                case WHITELIST -> isContain(artworkHTML);
                case BLACKLIST -> !isContain(artworkHTML);
            };
        }

        public boolean isContain(String artworkHTML) {
            for (String actualTag : getActualValue(artworkHTML))
                if (expectValue.contains(actualTag))
                    return true;
            return false;
        }

        public static List<String> getActualValue(String artworkHTML) {
            Matcher matcher = Pattern.compile("\"tags\":\\[([^{}]*?)]").matcher(artworkHTML);
            if (matcher.find()) return List.of(matcher.group(1).split(","));
            return List.of();
        }
    }
}
