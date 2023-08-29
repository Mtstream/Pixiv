package org.kyaru.pixiv.service.utils.requester;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.message.BasicHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestClient {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36 Edg/108.0.1462.54";
    private static final String REFERER = "https://www.pixiv.net";
    private final CloseableHttpClient httpClient;

    public RequestClient(String cookie, Proxy proxy) {
        this.httpClient = createHttpClient(cookie, proxy);
    }

    private CloseableHttpClient createHttpClient(String cookie, Proxy proxy) {
        return HttpClients.custom()
                .setDefaultHeaders(
                        List.of(
                                new BasicHeader("cookie", cookie),
                                new BasicHeader("User-Agent", USER_AGENT),
                                new BasicHeader("Referer", REFERER)
                        )
                )
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectionRequestTimeout(60, TimeUnit.SECONDS)
                                .setResponseTimeout(60, TimeUnit.SECONDS)
                                .build()
                )
                .setProxy(proxy.toHttpHost())
                .build();
    }

    private HttpEntity download(String httpUrl) {
        try {
            return this.httpClient.execute(new HttpGet(httpUrl)).getEntity();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T download(String httpUrl, ReturnType.IType<T> returnType) {
        HttpEntity httpEntity = download(httpUrl);
        return httpEntity != null ? returnType.convert(httpEntity) : null;
    }

    @SuppressWarnings("all")
    public <T> List<T> download(List<String> httpUrls, ReturnType.IType<T> type) {
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

    public record Proxy(String host, int port) {
        public static Proxy parseProxy(String str) {
            Matcher matcher = Pattern.compile("([^_]+)_([^_]+)").matcher(str);
            if (matcher.find()) {
                return new Proxy(matcher.group(1), Integer.parseInt(matcher.group(2)));
            }
            return null;
        }

        public HttpHost toHttpHost() {
            return new HttpHost(host, port);
        }

        @Override
        public String toString() {
            return host + "_" + port;
        }
    }
}
