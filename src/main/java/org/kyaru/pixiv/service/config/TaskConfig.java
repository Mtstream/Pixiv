package org.kyaru.pixiv.service.config;

import org.json.simple.JSONObject;
import org.kyaru.pixiv.service.body.process.TaskPacker;
import org.kyaru.pixiv.service.utils.file.FileOperator;
import org.kyaru.pixiv.service.utils.network.RequestClient;

import java.nio.file.Path;

public record TaskConfig(Path settingFile) {
    public TaskPacker getTaskPacker(String fileName) {
        JSONObject json = new FileOperator(settingFile).read();
        if (json.containsKey(JSON_KEY.NORMAL_FILE_PATH.path) && json.containsKey(JSON_KEY.R18_FILE_PATH.path)) {
            return TaskPacker.getDefault(
                    getRequestClient(),
                    json.get(JSON_KEY.NORMAL_FILE_PATH.path).toString().formatted(fileName),
                    json.get(JSON_KEY.R18_FILE_PATH.path).toString().formatted(fileName)
            );
        } else {
            throw new RuntimeException("please configure task_config fully");
        }
    }

    public RequestClient getRequestClient() {
        JSONObject json = new FileOperator(settingFile).read();
        if (json.containsKey(JSON_KEY.PROXY.path) && json.containsKey(JSON_KEY.COOKIE.path)) {
            return new RequestClient(
                    json.get(JSON_KEY.COOKIE.path).toString(),
                    RequestClient.Proxy.parseProxy(json.get(JSON_KEY.PROXY.path).toString())
            );
        } else {
            throw new RuntimeException("please configure task_config fully");
        }
    }

    public static class Builder {
        private final FileOperator file;
        private JSONObject jsonObject ;
        public Builder(Path settingFile) {
            this.file = new FileOperator(settingFile);
            this.jsonObject = file.read();
        }

        public Builder setDefault(String cookie) {
            return this
                    .setNormalArtworkPath("Artwork/%s/NOR")
                    .setR18ArtworkPath("Artwork/%s/R18")
                    .setCookie(cookie)
                    .setProxy("-1", -1);
        }

        public Builder setNormalArtworkPath(String path) {
            return set(JSON_KEY.NORMAL_FILE_PATH, path);
        }

        public Builder setR18ArtworkPath(String path) {
            return set(JSON_KEY.R18_FILE_PATH, path);
        }

        public Builder setCookie(String cookie) {
            return set(JSON_KEY.COOKIE, cookie);
        }

        public Builder setProxy(String host, int port) {
            return set(JSON_KEY.PROXY, new RequestClient.Proxy(host, port).toString());
        }

        private Builder set(JSON_KEY key, String value) {
            this.jsonObject.put(key.path, value);
            return this;
        }

        public Path confirm() {
            this.file.write(this.jsonObject);
            return this.file.filePath();
        }
    }

    private enum JSON_KEY {
        NORMAL_FILE_PATH("normal_file_path"),
        R18_FILE_PATH("r18_file_path"),
        COOKIE("cookie"),
        PROXY("proxy");
        private final String path;

        JSON_KEY(String path) {
            this.path = path;
        }
    }
}
