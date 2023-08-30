package org.kyaru.pixiv.service.download.collect;

import org.jsoup.Jsoup;
import org.kyaru.pixiv.service.download.Downloader;
import org.kyaru.pixiv.service.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;
import org.seimicrawler.xpath.JXDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchByAuthor implements Downloader.Scheme {
    private static final Pattern IDS_PATTERN = Pattern.compile("\\d+");
    private static final String GET_NAME_URL = "https://www.pixiv.net/users/%s";
    private static final Pattern NAME_PATTERN = Pattern.compile("(.*?).-.pixiv");
    private static final String GET_ID_URL = "https://www.pixiv.net/search_user.php?nick=%s&s_mode=s_usr";
    private static final Pattern ID_PATTERN = Pattern.compile("\\w+/(\\d+)");
    private static final String GET_NAME_XPATH = "//head/title/text()";
    private static final String GET_ID_XPATH = "//h1/a[@target=\"_blank\"][@class=\"title\"]/@href";
    private final String authorInfo;
    private String authorName;
    private String authorID;

    public SearchByAuthor(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    private void init(RequestClient requestClient) {
        boolean isArtworkID = !this.authorInfo.matches("[^0-9]");
        String url = isArtworkID ? GET_NAME_URL.formatted(authorInfo) : GET_ID_URL.formatted(authorInfo);
        String html = requestClient.download(url, ReturnType.STRING);
        JXDocument jxDoc = new JXDocument(Jsoup.parse(html).getAllElements());
        String another = jxDoc.selNOne(isArtworkID ? GET_NAME_XPATH : GET_ID_XPATH).asString();
        Matcher matcher = isArtworkID ? NAME_PATTERN.matcher(another) : ID_PATTERN.matcher(another);
        if (matcher.find()) {
            this.authorID = isArtworkID ? this.authorInfo : matcher.group(1);
            this.authorName = isArtworkID ? matcher.group(1) : this.authorInfo;
        }
    }

    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/user/%s/profile/all?".formatted(this.authorID);
        String json = requestClient.download(url, ReturnType.STRING);
        String ids = JSONUtil.select(json, "body", "illusts").toString();
        Matcher matcher = IDS_PATTERN.matcher(ids);
        List<String> idList = new ArrayList<>();
        while (matcher.find()) {
            idList.add(matcher.group());
        }
        idList = idList.size() > sourceLimit ? idList.subList(0, sourceLimit) : idList;
        return idList;
    }

    public String getFileName() {
        return this.authorName;
    }

    @Override
    public Output get(RequestClient requestClient, int sourceLimit) {
        init(requestClient);
        return new Output(getArtworkIDs(requestClient, sourceLimit), getFileName());
    }
}
