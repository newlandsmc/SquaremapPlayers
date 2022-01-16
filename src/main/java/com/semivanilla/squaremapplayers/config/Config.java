package com.semivanilla.squaremapplayers.config;

import com.google.common.base.Throwables;
import com.semivanilla.squaremapplayers.SquaremapPlayers;
import org.bukkit.Bukkit;
import org.checkerframework.checker.units.qual.C;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Config {

    private static final Pattern PATH_PATTERN = Pattern.compile("\\.");
    private static final String HEADER = "";

    private static File CONFIG_FILE;
    public static ConfigurationNode config;
    public static YamlConfigurationLoader configLoader;
    static int VERSION;

    public static void reload() {
        CONFIG_FILE = new File(SquaremapPlayers.getInstance().getDataFolder(), "config.conf");
        configLoader = YamlConfigurationLoader.builder()
                .file(CONFIG_FILE)
                .nodeStyle(NodeStyle.BLOCK)
                .build();
        if (!CONFIG_FILE.getParentFile().exists()) {
            if(!CONFIG_FILE.getParentFile().mkdirs()) {
                return;
            }
        }
        if (!CONFIG_FILE.exists()) {
            try {
                if(!CONFIG_FILE.createNewFile()) {
                    return;
                }
            } catch (IOException error) {
                error.printStackTrace();
            }
        }
        try {
            config = configLoader.load(ConfigurationOptions.defaults().shouldCopyDefaults(false));
        } catch (IOException ex) {
            Bukkit.getLogger().severe("Could not load config.yml, please correct your syntax errors");
            throw new RuntimeException(ex);
        }

        VERSION = getInt("config-version", 1);

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
        saveConfig();
    }

    public static void saveConfig() {
        try {
            configLoader.save(config);
        } catch (IOException ex) {
            throw Throwables.propagate(ex.getCause());
        }
    }

    private static Object[] splitPath(String key) {
        return PATH_PATTERN.split(key);
    }

    private static void set(String path, Object def) {
        if(config.node(splitPath(path)).virtual()) {
            try {
                config.node(splitPath(path)).set(def);
            } catch (SerializationException ignore) {
            }
        }
    }

    private static void setString(String path, String def) {
        try {
            if(config.node(splitPath(path)).virtual())
                config.node(splitPath(path)).set(io.leangen.geantyref.TypeToken.get(String.class), def);
        } catch(SerializationException ignore) {
        }
    }

    private static String getString(String path, String def) {
        setString(path, def);
        return config.node(splitPath(path)).getString(def);
    }

    private static boolean getBoolean(String path, boolean def) {
        set(path, def);
        return config.node(splitPath(path)).getBoolean(def);
    }

    private static int getInt(String path, int def) {
        set(path, def);
        return config.node(splitPath(path)).getInt(def);
    }

    private static double getDouble(String path, double def) {
        set(path, def);
        return config.node(splitPath(path)).getDouble(def);
    }

    private static Color getColor(String path, Color def) {
        set(path, colorToHex(def));
        return hexToColor(config.node(splitPath(path)).getString(colorToHex(def)));
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

    public static int updateInterval = 300;
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

    public static Color bountyColor = Color.RED;
    public static int bountyWeight = 1;
    public static double bountyOpacity = 1.0D;
    public static Color bountyFillColor = Color.ORANGE;
    public static double bountyFillOpacity = 0.2D;
    public static String bountyHoverToolTip = "";
    public static String bountyClickTooltip = "";


    private static void marketSettings() {
        color = getColor("marker.color", color);
        weight = getInt("marker.weight", weight);
        opacity = getDouble("marker.opacity", opacity);
        fillColor = getColor("marker.fill-color", fillColor);
        fillOpacity = getDouble("marker.fill-opacity", fillOpacity);
        hoverTooltip = getString("marker.hover-tooltip", hoverTooltip);
        clickTooltip = getString("marker.click-tooltip", clickTooltip);

        bountyColor = getColor("bounty.color", bountyColor);
        bountyWeight = getInt("bounty.weight", bountyWeight);
        bountyOpacity = getDouble("bounty.opacity", bountyOpacity);
        bountyFillColor = getColor("bounty.fill-color", bountyFillColor);
        bountyFillOpacity = getDouble("bounty.fill-opacity", bountyFillOpacity);
        bountyHoverToolTip = getString("bounty.hover-tooltip", bountyHoverToolTip);
        bountyClickTooltip = getString("bounty.click-tooltip", bountyClickTooltip);

    }

}
