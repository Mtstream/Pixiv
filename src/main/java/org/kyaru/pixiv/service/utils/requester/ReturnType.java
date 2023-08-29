package org.kyaru.pixiv.service.utils.requester;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

public class ReturnType {
    public static final IType<String> STRING = httpEntity -> {
        try {
            return EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException | ParseException ex) {
            throw new RuntimeException(ex);
        }
    };

    public static final IType<byte[]> BYTES = httpEntity -> {
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


