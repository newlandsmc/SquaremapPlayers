package com.semivanilla.squaremapplayers.wrapper;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.jpenilla.squaremap.api.marker.Marker;

public class PlayerWrapper {

    private Location location;
    private String markerid;
    private Marker marker;

    public PlayerWrapper(Player player) {
        location = player.getLocation();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getMarkerid() {
        return markerid;
    }

    public void setMarkerid(String markerid) {
        this.markerid = markerid;
    }
}
