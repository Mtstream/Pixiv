package org.kyaru.pixiv.service.runner;

import java.awt.image.BufferedImage;
import java.util.List;

public record Item() {
    public enum Format implements Focus {
        IMG, GIF
    }

    public enum Tag implements Focus {
        NOR, R18
    }

    interface Focus {
    }

    public record ArtworkID(Tag tag, Format format, String artworkID) {
    }

    public record SaveData(Tag tag, Format format, List<String> nameList, List<BufferedImage> imageList) {
    }
}
