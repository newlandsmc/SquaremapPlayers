package com.semivanilla.squaremapplayers.config;

import xyz.jpenilla.squaremap.api.MapWorld;
import xyz.jpenilla.squaremap.api.WorldIdentifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class WorldConfig {
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

    private final String worldName;

    public WorldConfig(MapWorld world) {
        this.worldName = world.name();
        init();
    }

    public void init() {
        Config.readConfig(WorldConfig.class, this);
    }

    private void set(String path, Object val) {
        Config.CONFIG.addDefault("world-settings.default." + path, val);
        Config.CONFIG.set("world-settings.default." + path, val);
        if (Config.CONFIG.get("world-settings." + worldName + "." + path) != null) {
            Config.CONFIG.addDefault("world-settings." + worldName + "." + path, val);
            Config.CONFIG.set("world-settings." + worldName + "." + path, val);
        }
    }

    private boolean getBoolean(String path, boolean def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getBoolean("world-settings." + worldName + "." + path, Config.CONFIG.getBoolean("world-settings.default." + path));
    }

    private int getInt(String path, int def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getInt("world-settings." + worldName + "." + path, Config.CONFIG.getInt("world-settings.default." + path));
    }

    private String getString(String path, String def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getString("world-settings." + worldName + "." + path, Config.CONFIG.getString("world-settings.default." + path));
    }

    <T> List<?> getList(String path, T def) {
        Config.CONFIG.addDefault("world-settings.default." + path, def);
        return Config.CONFIG.getList("world-settings." + worldName + "." + path,
                Config.CONFIG.getList("world-settings.default." + path));
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