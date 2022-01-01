package com.semivanilla.squaremapplayers;

import com.semivanilla.squaremapplayers.config.Config;
import com.semivanilla.squaremapplayers.hook.SquaremapHook;
import org.bukkit.plugin.java.JavaPlugin;

public class SquaremapPlayers extends JavaPlugin {

    private static SquaremapPlayers instance;
    private SquaremapHook squaremapHook;

    @Override
    public void onEnable() {
        instance = this;
        Config.reload();
        squaremapHook = new SquaremapHook(this);
        squaremapHook.load();
    }

    @Override
    public void onDisable() {
        if (squaremapHook != null) {
            squaremapHook.disable();
        }
        squaremapHook = null;
    }

    public static SquaremapPlayers getInstance() {
        return instance;
    }

}
