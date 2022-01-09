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
    private static final String BOUNTY_META = "bounty";
    private static final int MIN_RADIUS = 10;

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
            if(isBounty(player))
                handleBountyPlayer(player,player.getLocation());
            else this.handlePlayer(player, player.getLocation());
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
        int z = loc.getBlockZ();
        double playerRad = (float)Config.radius / 2;
        double randomX = ThreadLocalRandom.current().nextDouble(x - playerRad, x + playerRad);
        double randomY = ThreadLocalRandom.current().nextDouble(z - playerRad, z + playerRad);
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

    private void handleBountyPlayer(Player player, Location loc) {
        final UUID uuid = player.getUniqueId();
        final int killCount = player.getMetadata(BOUNTY_META).get(0).asInt();
        final String markerid = "player_" + player.getName() + "_id_" + uuid;
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
        int z = loc.getBlockZ();
        int multiplier = killCount*Config.radiusDemultiplier;
        int killRadius = Config.radius;
        if(multiplier < Config.radius){
            killRadius = Config.radius-multiplier;
        }else {
            killRadius = MIN_RADIUS;
        }
        double playerRad = (float)killRadius / 2;
        double randomX = ThreadLocalRandom.current().nextDouble(x - playerRad, x + playerRad);
        double randomY = ThreadLocalRandom.current().nextDouble(z - playerRad, z + playerRad);
        Point point = Point.point(randomX, randomY);
        Circle circle = Circle.circle(point, killRadius);

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.bountyColor)
                .strokeWeight(Config.bountyWeight)
                .strokeOpacity(Config.bountyOpacity)
                .fillColor(Config.bountyFillColor)
                .fillOpacity(Config.bountyFillOpacity);

        if (!Config.hoverTooltip.isBlank()) { // TODO : placeholders if this is requested
            options.hoverTooltip(Config.bountyHoverToolTip);
        }

        if (!Config.clickTooltip.isBlank()) { // TODO : placeholders if this is requested
            options.clickTooltip(Config.bountyClickTooltip);
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

    private boolean isBounty(Player player){
        return player.hasMetadata(BOUNTY_META);
    }

}