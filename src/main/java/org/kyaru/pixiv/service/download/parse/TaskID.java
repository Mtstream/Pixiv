package org.kyaru.pixiv.service.download.parse;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public interface TaskID {
    Label label();

    Step step();

    enum Step {INITIAL, AFTER_LABELING, AFTER_PARSING, AFTER_SAVING, FAIL}

    record Label(Tag tag, Format format) {
        public static Label DEFAULT = new Label(Tag.BOTH, Format.BOTH);

        public static Label parseLabel(String str) {
            Matcher matcher = Pattern.compile("([^_]+)_([^_]+)").matcher(str);
            if (matcher.find()) {
                return new Label(Tag.parseTag(matcher.group(1)), Format.parseFormat(matcher.group(2)));
            }
            return new Label(Tag.BOTH, Format.BOTH);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Label other) {
                return (this.tag == Tag.BOTH || this.tag == other.tag) && (this.format == Format.BOTH && this.format == other.format);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tag, format);
        }

        @Override
        public String toString() {
            return tag + "_" + format;
        }

        public enum Format {
            IMG, GIF, BOTH;

            public static Format parseFormat(String str) {
                return switch (str) {
                    case "IMG" -> Format.IMG;
                    case "GIF" -> Format.GIF;
                    default -> Format.BOTH;
                };
            }
        }

        public enum Tag {
            NOR, R18, BOTH;

            public static Tag parseTag(String str) {
                return switch (str) {
                    case "NOR" -> Tag.NOR;
                    case "R18" -> Tag.R18;
                    default -> Tag.BOTH;
                };
            }
        }
    }
}
