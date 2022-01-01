package com.semivanilla.squaremapplayers.config;

import com.semivanilla.squaremapplayers.SquaremapPlayers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
public class Config {
    private static File CONFIG_FILE;
    static YamlConfiguration CONFIG;
    static int VERSION;

    public static void reload() {
        CONFIG_FILE = new File(SquaremapPlayers.getInstance().getDataFolder(), "config.yml");
        CONFIG = new YamlConfiguration();
        try {
            CONFIG.load(CONFIG_FILE);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().severe("Could not load config.yml, please correct your syntax errors");
            throw new RuntimeException(ex);
        }
        CONFIG.options().copyDefaults(true);

        VERSION = getInt("config-version", 1);
        set("config-version", 1);

        readConfig(Config.class, null);

        WorldConfig.reload();
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().severe("Error invoking " + method);
                        ex.printStackTrace();
                    }
                }
            }
        }

        try {
            CONFIG.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().severe("Could not save " + CONFIG_FILE);
            ex.printStackTrace();
        }
    }

    private static void set(String path, Object val) {
        CONFIG.addDefault(path, val);
        CONFIG.set(path, val);
    }

    private static String getString(String path, String def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getString(path, CONFIG.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getBoolean(path, CONFIG.getBoolean(path));
    }

    private static int getInt(String path, int def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getInt(path, CONFIG.getInt(path));
    }

    private static double getDouble(String path, double def) {
        CONFIG.addDefault(path, def);
        return CONFIG.getDouble(path, CONFIG.getDouble(path));
    }

    private static Color getColor(String path, Color def) {
        CONFIG.addDefault(path, colorToHex(def));
        return hexToColor(CONFIG.getString(path, CONFIG.getString(path)));
    }

    private static String colorToHex(final Color color) {
        return Integer.toHexString(color.getRGB() & 0x00FFFFFF);
    }

    private static Color hexToColor(final String hex) {
        if (hex == null) {
            return Color.RED;
        }
        String stripped = hex.replace("#", "");
        int rgb = (int) Long.parseLong(stripped, 16);
        return new Color(rgb);
    }

    public static int updateInterval = 30;
    public static int radius = 250;
    private static void baseSettings() {
        updateInterval = getInt("update-interval", updateInterval);
        radius = getInt("radius", radius);
    }

    public static Color color = Color.cyan;
    public static int weight = 1;
    public static double opacity = 1.0D;
    public static Color fillColor = Color.cyan;
    public static double fillOpacity = 0.2D;
    public static String hoverTooltip = "";
    public static String clickTooltip = "";
    private static void marketSettings() {
        color = getColor("marker.color", color);
        weight = getInt("marker.weight", weight);
        opacity = getDouble("marker.opacity", opacity);
        fillColor = getColor("marker.fill-color", fillColor);
        fillOpacity = getDouble("marker.fill-opacity", fillOpacity);
        hoverTooltip = getString("marker.hover-tooltip", hoverTooltip);
        clickTooltip = getString("marker.click-tooltip", clickTooltip);
    }

}
