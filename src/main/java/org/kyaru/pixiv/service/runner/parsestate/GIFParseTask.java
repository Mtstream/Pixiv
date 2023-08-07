package org.kyaru.pixiv.service.runner.parsestate;

import org.jetbrains.annotations.NotNull;
import org.kyaru.pixiv.service.runner.Item;
import org.kyaru.pixiv.service.runner.TaskRunner;
import org.kyaru.pixiv.service.runner.TaskScheduler;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

public class GIFParseTask implements TaskRunner.TaskPiece<Item.ArtworkID, Item.SaveData> {
    private final RequestClient requestClient;

    GIFParseTask(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    public List<String> getSrcNames(String artworkID, String json) {
        List<Double> delayList = new ArrayList<>();
        JSONUtil.select(json, "body", "frames").toStringList().forEach(item -> {
            if (item != null) {
                String delayInfo = JSONUtil.select(item, "delay").toString();
                delayList.add(Double.parseDouble(delayInfo));
            }
        });
        int delayMs = (int) (delayList.stream().mapToDouble(Double::doubleValue).sum() / delayList.size());
        String name = "[GIFd%s]%s.gif".formatted(delayMs, artworkID);
        return Collections.singletonList(name);
    }

    public List<BufferedImage> getSrcImages(String json) {
        String zipUrl = JSONUtil.select(json, "body", "originalSrc").toString();
        byte[] bytes = this.requestClient.download(zipUrl, ContentType.BYTE);

        List<BufferedImage> srcBytesList = new ArrayList<>();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ZipInputStream zis = new ZipInputStream(bais)) {
            while (zis.getNextEntry() != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                srcBytesList.add(ImageIO.read(new ByteArrayInputStream(baos.toByteArray())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return srcBytesList;
    }

    @Override
    public TaskScheduler.TaskState<Item.SaveData> runTask(Item.@NotNull ArtworkID artworkID) {
        String url = "https://www.pixiv.net/ajax/illust/%s/ugoira_meta?lang=zh".formatted(artworkID.artworkID());
        String json = this.requestClient.download(url, ContentType.JSON);
        return new TaskScheduler.TaskState<Item.SaveData>(
                TaskScheduler.TaskState.Step.AFTER_PARSING,
                new Item.SaveData(artworkID.tag(), artworkID.format(), getSrcNames(artworkID.artworkID(), json), getSrcImages(json))
        );
    }
}