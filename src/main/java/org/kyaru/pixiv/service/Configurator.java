package org.kyaru.pixiv.service;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.kyaru.pixiv.service.ServiceInterface;

import java.io.*;
import java.util.Scanner;

class Configurator {
    public static final String SETTING_FILE_NAME = "settings.json";
    public static final File TARGET_FILE = new File(System.getProperty("user.dir"));
    public static final File SETTING_FILE = new File(TARGET_FILE + File.separator + SETTING_FILE_NAME);

    public static final String KEY_SOURCE_LIMIT = "source_limit";
    public static final String KEY_NORMAL_FILE_PATH = "normal_file_path";
    public static final String KEY_R18_FILE_PATH = "r18_file_path";
    public static final String KEY_COOKIE = "cookie";

    static {
        createSettingFile();
    }

    private static boolean createSettingFile() {
        if(!SETTING_FILE.exists()) {
            try {
                SETTING_FILE.createNewFile();
                storeSettingDataSet(new ServiceInterface.ParameterSet(-1, "", "", ""));
            } catch (IOException ignored) {}
        }

        return SETTING_FILE.exists();
    }

    public static void storeSettingDataSet(ServiceInterface.ParameterSet settingDataSet) throws IOException {
        JSONObject obj = new JSONObject();
        obj.put(KEY_SOURCE_LIMIT, preCheck(String.valueOf(settingDataSet.sourceLimit())));
        obj.put(KEY_NORMAL_FILE_PATH, preCheck(settingDataSet.norFilePath()));
        obj.put(KEY_R18_FILE_PATH, preCheck(settingDataSet.r18FilePath()));
        obj.put(KEY_COOKIE, preCheck(settingDataSet.cookie()));

        FileWriter fileWriter = new FileWriter(SETTING_FILE);
        fileWriter.write(obj.toJSONString());
        fileWriter.close();
    }

    private static String preCheck(String parameter){
        return parameter == null ? "" : parameter;
    }

    public static ServiceInterface.ParameterSet loadSettingDataSet() throws FileNotFoundException {
        if (!createSettingFile()) {
            return new ServiceInterface.ParameterSet(-1, "", "", "");
        }

        String json = readContent(SETTING_FILE);
        System.out.println(json);

        JSONObject obj = (JSONObject) JSONValue.parse(json);

        return new ServiceInterface.ParameterSet(
                Integer.valueOf((String) obj.get(KEY_SOURCE_LIMIT)),
                (String) obj.get(KEY_NORMAL_FILE_PATH),
                (String) obj.get(KEY_R18_FILE_PATH),
                (String) obj.get(KEY_COOKIE)
        );
    }

    public static String readContent(File file) {
        try {
            Scanner scanner = new Scanner(SETTING_FILE);
            StringBuilder json = new StringBuilder();
            while(scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
            return json.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
