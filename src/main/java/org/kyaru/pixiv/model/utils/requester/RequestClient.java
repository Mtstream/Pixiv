package org.kyaru.pixiv.model.utils.requester;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RequestClient {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.54";
    private static final String REFERER = "https://www.pixiv.net";
    private final HttpGet httpGet;

    public RequestClient(String cookie) {
        this.httpGet = getHttpGet(cookie);
        this.httpGet.setConfig(getRequestConfig());
    }

    private static HttpGet getHttpGet(String cookie) {
        HttpGet httpGet = new HttpGet();
        httpGet.setHeader("cookie", cookie);
        httpGet.setHeader("User-Agent", USER_AGENT);
        httpGet.setHeader("Referer", REFERER);
        return httpGet;
    }

    private static RequestConfig getRequestConfig() {
        int timeOutMs = 5000;
        return RequestConfig.custom()
                .setConnectionRequestTimeout(timeOutMs)
                .setSocketTimeout(timeOutMs)
                .build();
    }

    private HttpEntity download(String httpUrl) {
        try {
            HttpGet httpGet = (HttpGet) this.httpGet.clone();
            httpGet.setURI(new URI(httpUrl));
            HttpResponse httpResponse = HttpClients.createDefault().execute(httpGet);
            return httpResponse.getEntity();
        } catch (CloneNotSupportedException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T download(String httpUrl, ContentType.IType<T> type) {
        HttpEntity httpEntity = download(httpUrl);
        return httpEntity != null ? type.convert(httpEntity) : null;
    }

    @SuppressWarnings("all")
    public <T> List<T> download(List<String> httpUrls, ContentType.IType<T> type) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<?>> futureList = new ArrayList<>();
        List<T> resultList = new ArrayList<>();
        for (String httpUrl : httpUrls) {
            futureList.add(executorService.submit(() -> download(httpUrl, type)));
        }
        for (Future<?> future : futureList) {
            try {
                resultList.add((T) future.get());
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }
        return resultList;
    }
}
