package com.semivanilla.squaremapplayers.task;

import com.semivanilla.bounties.model.Bounty;
import com.semivanilla.squaremapplayers.SquaremapPlayers;
import com.semivanilla.squaremapplayers.config.Config;
import com.semivanilla.squaremapplayers.config.WorldConfig;
import com.semivanilla.squaremapplayers.wrapper.PlayerWrapper;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.jpenilla.squaremap.api.*;
import xyz.jpenilla.squaremap.api.marker.Circle;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;

import java.util.Map;
import java.util.Optional;
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

    private final double minX;
    private final double minZ;
    private final double maxX;
    private final double maxZ;

    private boolean stop;

    public SquaremapTask(MapWorld world, WorldConfig worldConfig, SimpleLayerProvider provider) {
        this.world = world;
        this.provider = provider;
        this.worldConfig = worldConfig;
        this.players = new ConcurrentHashMap<>();

        // calculating this once, hook into a worldborder expand event to make sure these don't change?
        WorldBorder worldBorder = BukkitAdapter.bukkitWorld(this.world).getWorldBorder();
        Location center = worldBorder.getCenter();
        double size = worldBorder.getSize();
        int overlap = Math.round( (float) Config.radius / 2) - Config.radius;
        minX = center.getX() - size / 2.0D - overlap;
        minZ = center.getZ() - size / 2.0D - overlap;
        maxX = center.getX() + size / 2.0D + overlap;
        maxZ = center.getZ() + size / 2.0D + overlap;
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
                handleBountyPlayer(player,player.getLocation(),false);
            else this.handlePlayer(player, player.getLocation(),false);
        }
    }

    public void handlePlayer(Player player, Location loc, boolean forceUpdate) {
        UUID uuid = player.getUniqueId();
        String markerid = "player_" + player.getName().replace("*","") + "_id_" + uuid;
        PlayerWrapper wrapper = players.get(uuid);
        if (!forceUpdate && wrapper != null) {
            if (worldConfig.persistVanished && (player.getGameMode() == GameMode.SPECTATOR || isVanished(player))) {
                this.provider.addMarker(Key.of(wrapper.getMarkerid()), wrapper.getMarker());
                return;
            }
            if (loc.getWorld().getName().equals(wrapper.getLocation().getWorld().getName())) {
                if (loc.distanceSquared(wrapper.getLocation()) < worldConfig.updateRadius * worldConfig.updateRadius) {
                    this.provider.addMarker(Key.of(wrapper.getMarkerid()), wrapper.getMarker());
                    return;
                }
            }
        }
        if (wrapper == null) {
            wrapper = new PlayerWrapper(player);
            players.put(uuid, wrapper);
        }
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        double playerRad = (float)Config.radius / 2;
        Point point = randomPoint(x, z, playerRad);
        Circle circle = Circle.circle(point, Config.radius);

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.color)
                .strokeWeight(Config.weight)
                .strokeOpacity(Config.opacity)
                .fillColor(Config.fillColor)
                .fillOpacity(Config.fillOpacity);

        if (!Config.hoverTooltip.isBlank()) {
            options.hoverTooltip(Config.hoverTooltip);
        }

        if (!Config.clickTooltip.isBlank()) {
            options.clickTooltip(Config.clickTooltip);
        }

        circle.markerOptions(options);

        this.provider.addMarker(Key.of(markerid), circle);
        wrapper.setLocation(loc); wrapper.setMarker(circle); wrapper.setMarkerid(markerid);
    }

    public void handleBountyPlayer(Player player, Location loc, boolean forceUpdate) {
        if(!isBounty(player)){
            handlePlayer(player,player.getLocation(),true);
        }

        final UUID uuid = player.getUniqueId();

        Optional<Bounty> bountyOptional = SquaremapPlayers.getInstance().getBountiesAPI().getBountyFor(player);
        if(bountyOptional.isEmpty())
            return;

        final int killRadius = Config.getKillRadius(bountyOptional.get().getKilled());
        final String markerid = "player_" + player.getName().replace("*","") + "_id_" + uuid;
        PlayerWrapper wrapper = players.get(uuid);
        if (wrapper != null) {
            if (loc.getWorld().getName().equals(wrapper.getLocation().getWorld().getName())) {
                if (!forceUpdate && loc.distanceSquared(wrapper.getLocation()) < worldConfig.updateRadius * worldConfig.updateRadius) {
                    this.provider.addMarker(Key.of(wrapper.getMarkerid()), wrapper.getMarker());
                    return;
                }
            }
        }
        if (wrapper == null) {
            wrapper = new PlayerWrapper(player);
            players.put(uuid, wrapper);
        }
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        double playerRad = (float)killRadius / 2;
        Point point = randomPoint(x, z, playerRad);
        Circle circle = Circle.circle(point, killRadius);

        MarkerOptions.Builder options = MarkerOptions.builder()
                .strokeColor(Config.bountyColor)
                .strokeWeight(Config.bountyWeight)
                .strokeOpacity(Config.bountyOpacity)
                .fillColor(Config.bountyFillColor)
                .fillOpacity(Config.bountyFillOpacity);

        if (!Config.hoverTooltip.isBlank()) {
            options.hoverTooltip(Config.bountyHoverToolTip);
        }

        if (!Config.clickTooltip.isBlank()) {
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
        if(SquaremapPlayers.getInstance().getBountiesAPI() != null)
            return SquaremapPlayers.getInstance().getBountiesAPI().isPlayerOnlineBounty(player);
        else return false;
    }

    private Point randomPoint(int x, int z, double radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double randomX = clamp(random.nextDouble(x - radius, x + radius), minX, maxX);
        double randomY = clamp(random.nextDouble(z - radius, z + radius), minZ, maxZ);
        return Point.point(randomX, randomY);
    }

    private double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else {
            return Math.min(value, max);
        }
    }

}