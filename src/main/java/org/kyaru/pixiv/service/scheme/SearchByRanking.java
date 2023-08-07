package org.kyaru.pixiv.service.scheme;

import org.kyaru.pixiv.service.ServiceInterface;
import org.kyaru.pixiv.service.utils.requester.ContentType;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchByRanking implements ServiceInterface.IScheme {
    private static final Pattern ID_PATTERN = Pattern.compile("\"data-type=\".*?\"data-id=\"(.*?)\"");

    public SearchByRanking() {
    }

    private List<String> parseIDs(RequestClient requestClient, String urls) {
        Matcher matcher = ID_PATTERN.matcher(requestClient.download(urls, ContentType.HTML));
        List<String> idList = new ArrayList<>();
        while (matcher.find()) {
            idList.add(matcher.group());
        }
        return idList;
    }

    @Override
    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String norURL = "https://www.pixiv.net/ranking.php?mode=daily";
        String r18URL = norURL + "_18";
        List<String> norIDList = this.parseIDs(requestClient, norURL).subList(0, sourceLimit / 2);
        List<String> r18IDList = this.parseIDs(requestClient, r18URL).subList(0, sourceLimit / 2);
        norIDList.addAll(r18IDList);
        return norIDList;
    }

    @Override
    public String getFileName(RequestClient requestClient) {
        return "Ranking";
    }
}
