package org.kyaru.pixiv.model.utils.jsonparser;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;
import java.util.List;

public class JSONUtil {
    public static JSONEntry select(String json, String nodes) {
        return select(json, nodes.split("/"));
    }

    public static JSONEntry select(String json, String... nodes) {
        Object jObj = JSONValue.parse(json);
        for (String node : nodes) {
            jObj = ((JSONObject) jObj).get(node);
        }
        return new JSONEntry(jObj);
    }

    public record JSONEntry(Object jsonObject) {
        public List<String> toStringList() {
            List<String> stringList = new ArrayList<>();
            ((JSONArray) jsonObject).forEach(json -> stringList.add(json.toString()));
            return stringList;
        }

        public String toString() {
            return jsonObject.toString();
        }

        public int toInteger() {
            return Integer.parseInt(jsonObject.toString());
        }

        public boolean toBoolean() {
            return (boolean) jsonObject;
        }
    }
}
