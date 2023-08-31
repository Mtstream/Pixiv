package org.kyaru.pixiv.service.utils.file;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public record FileOperator(Path filePath) {
    public FileOperator {
        if (!filePath.toFile().exists()) {
            try {
                filePath.toFile().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JSONObject read() {
        StringBuilder json = new StringBuilder();
        try {
            Scanner scanner = new Scanner(filePath);
            while (scanner.hasNextLine()) {
                json.append(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (JSONObject) JSONValue.parse(json.toString());
    }

    public void write(JSONObject jsonObject) {
        try {
            FileWriter fileWriter = new FileWriter(filePath.toFile());
            fileWriter.write(jsonObject.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
