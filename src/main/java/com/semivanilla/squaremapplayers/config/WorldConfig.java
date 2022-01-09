package com.semivanilla.squaremapplayers.config;

import org.spongepowered.configurate.serialize.SerializationException;
import xyz.jpenilla.squaremap.api.MapWorld;
import xyz.jpenilla.squaremap.api.WorldIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class WorldConfig {

    private static final Pattern PATH_PATTERN = Pattern.compile("\\.");

    private static final Map<WorldIdentifier, WorldConfig> configs = new HashMap<xyz.jpenilla.squaremap.api.WorldIdentifier, WorldConfig>();

    public static void reload() {
        configs.clear();
    }

    public static WorldConfig get(MapWorld world) {
        WorldConfig config = configs.get(world.identifier());
        if (config == null) {
            config = new WorldConfig(world);
            configs.put(world.identifier(), config);
        }
        return config;
    }

    private final String configPath;
    private final String defaultPath;

    public WorldConfig(MapWorld world) {
        this.defaultPath = "world-settings.default.";
        this.configPath = "world-settings." + world.name() + ".";
        init();
    }

    public void init() {
        Config.readConfig(WorldConfig.class, this);
    }

    public static Object[] splitPath(String key) {
        return PATH_PATTERN.split(key);
    }

    private static void set(String path, Object def) {
        if(Config.config.node(splitPath(path)).virtual()) {
            try {
                Config.config.node(splitPath(path)).set(def);
            } catch (SerializationException ex) {
            }
        }
    }

    private boolean getBoolean(String path, boolean def) {
        set(defaultPath + path, def);
        return Config.config.node(splitPath(configPath+path)).getBoolean(
                Config.config.node(splitPath(defaultPath +path)).getBoolean(def));
    }

    private int getInt(String path, int def) {
        set(defaultPath + path, def);
        return Config.config.node(splitPath(configPath+path)).getInt(
                Config.config.node(splitPath(defaultPath +path)).getInt(def));
    }

    private String getString(String path, String def) {
        set(defaultPath + path, def);
        return Config.config.node(splitPath(configPath+path)).getString(
                Config.config.node(splitPath(defaultPath +path)).getString(def));
    }

    public boolean enabled = true;

    private void worldSettings() {
        enabled = getBoolean("enabled", enabled);
    }

    public String layerLabel = "Players";
    public boolean layerShowControls = true;
    public boolean layerControlsHidden = false;
    private void layerSettings() {
        layerLabel = getString("layer.label", layerLabel);
        layerShowControls = getBoolean("layer.controls.enabled", layerShowControls);
        layerControlsHidden = getBoolean("layer.controls.hide-by-default", layerControlsHidden);
    }

    public boolean hideVanished = true;
    public int updateRadius = 4;
    private void playerSettings() {
        hideVanished = getBoolean("player.hide-vanished", hideVanished);
        updateRadius = getInt("player.update-radius", updateRadius);
    }

}