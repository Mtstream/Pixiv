package org.kyaru.pixiv.model.service.spiderscheme;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.kyaru.pixiv.model.utils.jsonparser.JSONUtil;
import org.kyaru.pixiv.model.utils.requester.ContentType;
import org.kyaru.pixiv.model.utils.requester.RequestClient;
import org.seimicrawler.xpath.JXDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchByAuthor implements IScheme {
    private static final Pattern NAME_PATTERN = Pattern.compile("(.*?) - pixiv");
    private static final Pattern ID_PATTERN = Pattern.compile("\\w+/(\\d+)");
    private static final Pattern IDS_PATTERN = Pattern.compile("\\d+");
    private final String authorInfo;

    public SearchByAuthor(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    private String getAuthorName(RequestClient requestClient) {
        boolean isNumber = !this.authorInfo.matches("[^0-9]");
        if (isNumber) {
            String url = "https://www.pixiv.net/users/" + this.authorInfo;
            String html = requestClient.download(url, ContentType.HTML);
            JXDocument jxDoc = new JXDocument(Jsoup.parse(html).getAllElements());
            String authorName = jxDoc.selNOne("//head/title/text()").asString();
            Matcher matcher = NAME_PATTERN.matcher(authorName);
            if (matcher.find()) {
                authorName = matcher.group(1);
            }
            return authorName;
        } else {
            return this.authorInfo;
        }
    }

    private String getAuthorId(RequestClient requestClient) {
        boolean isNumber = !authorInfo.matches("[^0-9]");
        if (!isNumber) {
            String url = "https://www.pixiv.net/search_user.php?nick=%s&s_mode=s_usr".formatted(this.authorInfo);
            String html = requestClient.download(url, ContentType.HTML);
            JXDocument jxDoc = new JXDocument(Jsoup.parse(html).getAllElements());
            String authorId = jxDoc.selNOne("//h1/a[@target=\"_blank\"][@class=\"title\"]/@href").asString();
            Matcher matcher = ID_PATTERN.matcher(authorId);
            if (matcher.find()) {
                authorId = matcher.group(1);
            }
            return authorId;
        } else {
            return this.authorInfo;
        }
    }

    @Override
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String url = "https://www.pixiv.net/ajax/user/%s/profile/all?".formatted(getAuthorId(requestClient));
        String json = requestClient.download(url, ContentType.JSON);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(json);
        String ids = JSONUtil.select(json, "body", "illusts").toString();
        Matcher matcher = IDS_PATTERN.matcher(ids);
        List<String> idList = new ArrayList<>();
        while (matcher.find()) {
            idList.add(matcher.group());
        }
        return idList;
    }

    @Override
    public String getFileName(RequestClient requestClient) {
        return getAuthorName(requestClient);
    }
}
