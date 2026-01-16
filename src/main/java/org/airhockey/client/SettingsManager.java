package org.airhockey.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    private static final String SETTINGS_FILE = "settings.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
            .create();

    public Settings loadSettings() {
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            Type type = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> map = gson.fromJson(reader, type);

            Settings settings = new Settings();

            if (map != null) {
                settings.setPort(((Number) map.getOrDefault("port", 7777)).intValue());
                settings.setPlayerName((String) map.getOrDefault("playerName", "Игрок"));
                settings.setGameSpeed(((Number) map.getOrDefault("gameSpeed", 1.0)).floatValue());
                settings.setShowFPS((Boolean) map.getOrDefault("showFPS", true));

                if (map.containsKey("puckColor")) {
                    settings.setPuckColor(parseColor((String) map.get("puckColor")));
                }
                if (map.containsKey("paddleColor")) {
                    settings.setPaddleColor(parseColor((String) map.get("paddleColor")));
                }
                if (map.containsKey("backgroundColor")) {
                    settings.setBackgroundColor(parseColor((String) map.get("backgroundColor")));
                }
            }

            return settings;

        } catch (FileNotFoundException e) {
            Settings defaultSettings = new Settings();
            saveSettings(defaultSettings);
            return defaultSettings;
        } catch (Exception e) {
            e.printStackTrace();
            return new Settings();
        }
    }

    public void saveSettings(Settings settings) {
        Map<String, Object> map = new HashMap<>();
        map.put("port", settings.getPort());
        map.put("playerName", settings.getPlayerName());
        map.put("gameSpeed", settings.getGameSpeed());
        map.put("showFPS", settings.isShowFPS());
        map.put("puckColor", colorToString(settings.getPuckColor()));
        map.put("paddleColor", colorToString(settings.getPaddleColor()));
        map.put("backgroundColor", colorToString(settings.getBackgroundColor()));
        map.put("lineColor", colorToString(settings.getLineColor()));

        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            gson.toJson(map, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Color parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("#")) {
                return Color.decode(colorStr);
            } else if (colorStr.startsWith("rgb")) {
                String[] parts = colorStr.substring(4, colorStr.length() - 1).split(",");
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return new Color(r, g, b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.WHITE;
    }

    private String colorToString(Color color) {
        return String.format("#%02x%02x%02x",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }
}