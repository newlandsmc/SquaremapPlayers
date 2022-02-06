package com.semivanilla.squaremapplayers;

import com.semivanilla.bounties.Bounties;
import com.semivanilla.bounties.api.BountiesAPI;
import com.semivanilla.squaremapplayers.config.Config;
import com.semivanilla.squaremapplayers.hook.SquaremapHook;
import com.semivanilla.squaremapplayers.listener.BountyEvents;
import org.bukkit.plugin.java.JavaPlugin;

public class SquaremapPlayers extends JavaPlugin {

    private static SquaremapPlayers instance;
    private SquaremapHook squaremapHook;
    private BountiesAPI bountiesAPI;

    @Override
    public void onEnable() {
        instance = this;
        Config.reload();
        squaremapHook = new SquaremapHook(this);
        squaremapHook.load();
        if(getServer().getPluginManager().isPluginEnabled("Bounties")){
            try {
                bountiesAPI = Bounties.getBountyAPI();
                getServer().getPluginManager().registerEvents(new BountyEvents(this),this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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

    public SquaremapHook getSquaremapHook() {
        return squaremapHook;
    }

    public BountiesAPI getBountiesAPI() {
        return bountiesAPI;
    }
}
