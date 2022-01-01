package com.semivanilla.squaremapplayers.task;

import com.semivanilla.squaremapplayers.config.Config;
import com.semivanilla.squaremapplayers.config.WorldConfig;
import com.semivanilla.squaremapplayers.wrapper.PlayerWrapper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Circle;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class SquaremapTask extends BukkitRunnable {
    private final MapWorld world;
    private final SimpleLayerProvider provider;
    private final WorldConfig worldConfig;
    private final Map<UUID, PlayerWrapper> players;

    private boolean stop;

    public SquaremapTask(MapWorld world, WorldConfig worldConfig, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
        this.worldConfig = worldConfig;
        this.players = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        if (this.stop) {
            this.cancel();
        }

        this.provider.clearMarkers();

        for (Player player : BukkitAdapter.bukkitWorld(this.world).getPlayers()) {
            if (worldConfig.hideVanished && (player.getGameMode() == GameMode.SPECTATOR || isVanished(player))) {
                continue;
            }
            this.handlePlayer(player, player.getLocation());
        }
    }

    private void handlePlayer(Player player, Location loc) {
        UUID uuid = player.getUniqueId();
        String markerid = "player_" + player.getName() + "_id_" + uuid;
        PlayerWrapper wrapper = players.get(uuid);
        if (wrapper != null) {
            if (loc.distanceSquared(wrapper.getLocation()) < worldConfig.updateRadius * worldConfig.updateRadius) {
                this.provider.addMarker(Key.of(wrapper.getMarkerid()), wrapper.getMarker());
                return;
            }
        }
        if (wrapper == null) {
            wrapper = new PlayerWrapper(player);
            players.put(uuid, wrapper);
        }
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        double playerRad = (float)Config.radius / 2;
        double randomX = ThreadLocalRandom.current().nextDouble(x - playerRad, x + playerRad);
        double randomY = ThreadLocalRandom.current().nextDouble(y - playerRad, y + playerRad);
        Point point = Point.point(randomX, randomY);
        Circle circle = Circle.circle(point, Config.radius);

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.color)
                .strokeWeight(Config.weight)
                .strokeOpacity(Config.opacity)
                .fillColor(Config.fillColor)
                .fillOpacity(Config.fillOpacity);

        if (!Config.hoverTooltip.isBlank()) { // TODO : placeholders if this is requested
            options.hoverTooltip(Config.hoverTooltip);
        }

        if (!Config.clickTooltip.isBlank()) { // TODO : placeholders if this is requested
            options.clickTooltip(Config.clickTooltip);
        }

        circle.markerOptions(options);

        this.provider.addMarker(Key.of(markerid), circle);
        wrapper.setLocation(loc); wrapper.setMarker(circle); wrapper.setMarkerid(markerid);
    }

    public void disable() {
        this.cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }

    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

}