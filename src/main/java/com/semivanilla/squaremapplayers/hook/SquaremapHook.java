package com.semivanilla.squaremapplayers.hook;

import java.util.HashMap;
import java.util.Map;

import com.semivanilla.squaremapplayers.SquaremapPlayers;
import com.semivanilla.squaremapplayers.config.Config;
import com.semivanilla.squaremapplayers.config.WorldConfig;
import com.semivanilla.squaremapplayers.task.SquaremapTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.MapWorld;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.SquaremapProvider;
import xyz.jpenilla.squaremap.api.WorldIdentifier;

public final class SquaremapHook {
    private static final Key PLAYER_RADIUS_LAYER = Key.of("PlayerRadius");

    private final Map<WorldIdentifier, SquaremapTask> tasks = new HashMap<>();
    private final SquaremapPlayers plugin;

    public SquaremapHook(SquaremapPlayers plugin) {
        this.plugin = plugin;
    }

    public void load() {
        for (MapWorld mapWorld : SquaremapProvider.get().mapWorlds()) {
            WorldConfig worldConfig = WorldConfig.get(mapWorld);
            if (!worldConfig.enabled) {
                continue;
            }

            final SimpleLayerProvider provider = SimpleLayerProvider.builder(worldConfig.layerLabel)
                    .showControls(worldConfig.layerShowControls)
                    .defaultHidden(worldConfig.layerControlsHidden)
                    .build();

            mapWorld.layerRegistry().register(PLAYER_RADIUS_LAYER, provider);
            final SquaremapTask task = new SquaremapTask(mapWorld, worldConfig, provider);
            task.runTaskTimer(this.plugin, 0, Config.updateInterval);
            this.tasks.put(mapWorld.identifier(), task);
        }
    }

    public void disable() {
        this.tasks.values().forEach(SquaremapTask::disable);
        this.tasks.clear();
    }

    public void updateStatusFor(@NotNull Player player, boolean newBounty){
        for (MapWorld mapWorld : SquaremapProvider.get().mapWorlds()) {
            if(newBounty){
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        tasks.get(mapWorld.identifier()).handleBountyPlayer(player,player.getLocation(),true);
                    }
                },20);

            }
            else {
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        tasks.get(mapWorld.identifier()).handlePlayer(player, player.getLocation(),true);
                    }
                },20);

            }
        }
    }
}