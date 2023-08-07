package org.kyaru.pixiv.service.utils.requester;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ContentType {
    public static final IType<String> HTML = httpEntity -> {
        try {
            return EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    };
    public static final IType<String> JSON = httpEntity -> {
        try {
            return EntityUtils.toString(httpEntity, "utf-8").trim();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    };
    public static final IType<byte[]> BYTE = httpEntity -> {
        try {
            return EntityUtils.toByteArray(httpEntity);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    };

    @FunctionalInterface
    interface IType<T> {
        T convert(HttpEntity httpEntity);
    }
}


