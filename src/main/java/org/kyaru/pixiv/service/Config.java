package org.kyaru.pixiv.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kyaru.pixiv.service.download.parse.TaskID;
import org.kyaru.pixiv.service.utils.requester.RequestClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Config {
    private static final File SETTING_FILE = Path.of(System.getProperty("user.dir"), "settings.json").toFile();

    private static Config config = null;

    private final Getter getter;
    private final Setter setter;

    public static Config getConfig() {
        if (config == null) {
            config = new Config(SETTING_FILE);
        }
        return config;
    }

    private Config(File settingFile) {
        try {
            if (!settingFile.exists()) {
                settingFile.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getter = new Getter(settingFile);
        this.setter = new Setter(settingFile);
    }

    public Setter setter() {
        return setter;
    }

    public Getter getter() {
        return getter;
    }

    private enum JSON {
        SOURCE_LIMIT("source_limit"),
        NORMAL_FILE_PATH("normal_file_path"),
        R18_FILE_PATH("r18_file_path"),
        COOKIE("cookie"),
        PROXY("proxy"),
        LABEL("label");
        private final String path;

        JSON(String path) {
            this.path = path;
        }
    }

    public static class Setter {
        private final File settingFile;
        private Setter(File settingFile) {
            this.settingFile = settingFile;
        }

        public Setter setDefault(String cookie) {
            return this.setAcceptableLabel(TaskID.Label.Tag.BOTH, TaskID.Label.Format.BOTH)
                    .setArtworkCount(50)
                    .setNormalArtworkPath("Artwork/%s/NOR")
                    .setR18ArtworkPath("Artwork/%s/R18")
                    .setCookie(cookie)
                    .setProxy("-1", -1);
        }

        public Setter setArtworkCount(int count) {
            return set(JSON.SOURCE_LIMIT, Integer.toString(count));
        }

        public Setter setNormalArtworkPath(String path) {
            return set(JSON.NORMAL_FILE_PATH, path);
        }

        public Setter setR18ArtworkPath(String path) {
            return set(JSON.R18_FILE_PATH, path);
        }

        public Setter setCookie(String cookie) {
            return set(JSON.COOKIE, cookie);
        }

        public Setter setProxy(String host, int port) {
            return set(JSON.PROXY, new RequestClient.Proxy(host, port).toString());
        }

        public Setter setAcceptableLabel(TaskID.Label.Tag tag, TaskID.Label.Format format) {
            return set(JSON.LABEL, new TaskID.Label(tag, format).toString());
        }

        private Setter set(JSON key, String value) {
            JSONObject jsonObject = Config.getConfig().getter().getJsonObject();
            jsonObject.put(key.path, value);
            try {
                FileWriter fileWriter = new FileWriter(settingFile);
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return this;
        }
    }

    public static class Getter {
        private final File settingFile;
        private Getter(File settingFile) {
            this.settingFile = settingFile;
        }

        public JSONObject getJsonObject() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON.SOURCE_LIMIT.path, Integer.toString(getArtworkCount()));
            jsonObject.put(JSON.NORMAL_FILE_PATH.path, getNormalArtworkPath());
            jsonObject.put(JSON.R18_FILE_PATH.path, getR18ArtworkPath());
            jsonObject.put(JSON.COOKIE.path, getCookie());
            jsonObject.put(JSON.PROXY.path, turnToString(getProxy()));
            jsonObject.put(JSON.LABEL.path, turnToString(getAcceptableLabel()));
            return jsonObject;
        }

        public int getArtworkCount() {
            return Integer.parseInt(get(JSON.SOURCE_LIMIT));
        }

        public String getNormalArtworkPath() {
            return get(JSON.NORMAL_FILE_PATH);
        }

        public String getR18ArtworkPath() {
            return get(JSON.R18_FILE_PATH);
        }

        public String getCookie() {
            return get(JSON.COOKIE);
        }

        public RequestClient.Proxy getProxy() {
            return RequestClient.Proxy.parseProxy(get(JSON.PROXY));
        }

        public TaskID.Label getAcceptableLabel() {
            return TaskID.Label.parseLabel(get(JSON.LABEL));
        }

        private String get(JSON key) {
            StringBuilder json = new StringBuilder();
            try {
                Scanner scanner = new Scanner(settingFile);
                while (scanner.hasNextLine()) {
                    json.append(scanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            JSONObject jsonObject = (JSONObject) JSONValue.parse(json.toString());
            if (jsonObject == null) {
                return "-1";
            }
            String result = (String) jsonObject.get(key.path);
            return result == null ? "-1" : result;
        }
    }

    private static String turnToString(Object o) {
        return o == null ? "-1" : o.toString();
    }
}