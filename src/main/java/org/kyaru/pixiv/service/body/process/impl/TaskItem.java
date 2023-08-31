package org.kyaru.pixiv.service.body.process.impl;

import org.kyaru.pixiv.service.body.process.TaskID;

import java.awt.image.BufferedImage;
import java.util.List;

public record TaskItem() {
    public record ArtworkID(String artworkID, TaskID.Step step, TaskID.Label label) implements TaskID {
        @Override
        public String toString() {
            return "%s | ArtworkID{%s}".formatted(step, artworkID);
        }
    }

    public record SaveData(List<String> nameList, List<BufferedImage> imageList, TaskID.Step step, Label label) implements TaskID {
        @Override
        public String toString() {
            return "%s | %s".formatted(step, nameList);
        }
    }
}
