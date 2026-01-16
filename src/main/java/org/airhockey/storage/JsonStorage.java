package org.airhockey.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JsonStorage {
    private static final String FILE = "history.json";
    private static final Gson gson = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Map<String, Object>>>(){}.getType();

    public static List<Map<String, Object>> loadHistory() {
        try (FileReader reader = new FileReader(FILE)) {
            return gson.fromJson(reader, LIST_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveMatch(String winner, int leftScore, int rightScore) {
        try {
            List<Map<String, Object>> history = loadHistory();

            Map<String, Object> record = new HashMap<>();
            record.put("winner", winner);
            record.put("leftScore", leftScore);
            record.put("rightScore", rightScore);
            record.put("time", System.currentTimeMillis());

            history.add(record);

            try (FileWriter writer = new FileWriter(FILE)) {
                gson.toJson(history, writer);
            }

            System.out.println("Матч сохранен!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}