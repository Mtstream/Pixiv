package org.kyaru.pixiv.service.download.parse.impl;

import org.kyaru.pixiv.service.download.parse.TaskID;

import java.awt.image.BufferedImage;
import java.util.List;

public record TaskItem() {
    public record ArtworkID(String artworkID, TaskID.Step step, TaskID.Label label) implements TaskID {
        @Override
        public String toString() {
            return "ArtworkID{%s} | %s ".formatted(artworkID, step);
        }
    }

    public record SaveData(List<String> nameList, List<BufferedImage> imageList, TaskID.Step step,
                           Label label) implements TaskID {
        @Override
        public String toString() {
            return "FileName{%s} | %s".formatted(nameList, step);
        }
    }
}
