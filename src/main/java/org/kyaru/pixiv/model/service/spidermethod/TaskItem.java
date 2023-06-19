package org.kyaru.pixiv.model.service.spidermethod;

import java.awt.image.BufferedImage;
import java.util.List;

public record TaskItem() {
    interface Focus {
    }

    public enum Format implements Focus {
        IMG, GIF;
    }

    public enum Tag implements Focus {
        NOR, R18;
    }

    public record Type(Format format, Tag tag) {
        public final static Type NOR_IMG = new Type(Format.IMG, Tag.NOR);
        public final static Type R18_IMG = new Type(Format.IMG, Tag.R18);
        public final static Type NOR_GIF = new Type(Format.GIF, Tag.NOR);
        public final static Type R18_GIF = new Type(Format.GIF, Tag.R18);

        @Override
        public int hashCode() {
            return format.hashCode() - tag.hashCode();
        }

        @Override
        public String toString() {
            return "%s_%s".formatted(tag, format);
        }
    }

    public record SaveData(List<String> nameList, List<BufferedImage> imageList, TaskItem.Type type) {
        @Override
        public String toString() {
            return type.toString() + nameList.toString();
        }
    }
}
