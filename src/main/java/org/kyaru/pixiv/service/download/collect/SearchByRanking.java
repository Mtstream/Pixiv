package org.kyaru.pixiv.service.download.collect;

import org.kyaru.pixiv.service.download.Downloader;
import org.kyaru.pixiv.service.utils.requester.RequestClient;
import org.kyaru.pixiv.service.utils.requester.ReturnType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchByRanking implements Downloader.Scheme {
    private static final Pattern ID_PATTERN = Pattern.compile("\"data-type=\".*?\"data-id=\"(.*?)\"");

    public SearchByRanking() {
    }

    private List<String> parseIDs(RequestClient requestClient, String urls) {
        Matcher matcher = ID_PATTERN.matcher(requestClient.download(urls, ReturnType.STRING));
        List<String> idList = new ArrayList<>();
        while (matcher.find()) {
            idList.add(matcher.group());
        }
        return idList;
    }

    public List<String> getArtworkIDs(RequestClient requestClient, int sourceLimit) {
        String norURL = "https://www.pixiv.net/ranking.php?mode=daily";
        String r18URL = norURL + "_18";
        List<String> norIDList = this.parseIDs(requestClient, norURL);
        List<String> r18IDList = this.parseIDs(requestClient, r18URL);
        if (norIDList.size() > sourceLimit / 2) {
            norIDList = norIDList.subList(0, sourceLimit / 2);
        }
        if (r18IDList.size() > sourceLimit / 2) {
            r18IDList = r18IDList.subList(0, sourceLimit / 2);
        }
        norIDList.addAll(r18IDList);
        return norIDList;
    }

    public String getFileName() {
        return "Ranking";
    }

    @Override
    public Output get(RequestClient requestClient, int sourceLimit) {
        return new Output(getArtworkIDs(requestClient, sourceLimit), getFileName());
    }
}
