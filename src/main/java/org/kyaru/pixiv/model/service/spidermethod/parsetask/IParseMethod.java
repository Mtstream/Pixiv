package org.kyaru.pixiv.model.service.spidermethod.parsetask;

import org.kyaru.pixiv.model.service.spidermethod.TaskItem;
import org.kyaru.pixiv.model.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.model.utils.requester.ContentType;
import org.kyaru.pixiv.model.utils.requester.RequestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

class ParseGIFMethod implements ParseTask.ParseMethod {
    public static List<String> getSrcNames(String id, RequestClient client, String json) {
        List<Double> delayList = new ArrayList<>();
        JSONUtil.select(json, "body", "frames").toStringList().forEach(item -> {
            if (item != null) {
                String delayInfo = JSONUtil.select(item, "delay").toString();
                delayList.add(Double.parseDouble(delayInfo));
            }
        });
        int delayMs = (int) (delayList.stream().mapToDouble(Double::doubleValue).sum() / delayList.size());
        String name = "[GIFd%s]%s.gif".formatted(delayMs, id);
        return Collections.singletonList(name);
    }

    public static List<BufferedImage> getSrcImages(String id, RequestClient requestClient, String json) {
        String zipUrl = JSONUtil.select(json, "body", "originalSrc").toString();
        byte[] bytes = requestClient.download(zipUrl, ContentType.BYTE);

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
    public TaskItem.SaveData getSaveData(String id, RequestClient client) {
        String url = "https://www.pixiv.net/ajax/illust/%s/ugoira_meta?lang=zh".formatted(id);
        String json = client.download(url, ContentType.JSON);
        return new TaskItem.SaveData(
                getSrcNames(id, client, json),
                getSrcImages(id, client, json),
                null
        );
    }
}

class ParseIMGMethod implements ParseTask.ParseMethod  {
    public List<String> getSrcNames(String id, RequestClient client, List<String> srcURLs) {
        List<String> nameList = new ArrayList<>();
        int page = 0;
        for (String srcURL : srcURLs) {
            String suffix = srcURL.substring(srcURLs.get(page).length() - 3);
            nameList.add(String.format("[IMG]%s_p%d.%s", id, page, suffix));
            page++;
        }
        return nameList;
    }

    public List<BufferedImage> getSrcImages(String id, RequestClient requestClient, List<String> srcURLs) {
        List<byte[]> bytesList = requestClient.download(srcURLs, ContentType.BYTE);
        List<BufferedImage> bufferedImageList = new ArrayList<>();
        for (byte[] bytes : bytesList) {
            try {
                bufferedImageList.add(ImageIO.read(new ByteArrayInputStream(bytes)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return bufferedImageList;
    }

    @Override
    public TaskItem.SaveData getSaveData(String id, RequestClient client) {
        List<String> srcURLs = new ArrayList<>();
        String url = "https://www.pixiv.net/ajax/illust/%s/pages?lang=zh".formatted(id);
        String json = client.download(url, ContentType.JSON);
        JSONUtil.select(json, "body").toStringList().forEach(node -> {
            String srcURL = JSONUtil.select(node.toString(), "urls", "original").toString();
            srcURLs.add(srcURL);
        });
        return new TaskItem.SaveData(
                getSrcNames(id, client, srcURLs),
                getSrcImages(id, client, srcURLs),
                null
        );
    }
}