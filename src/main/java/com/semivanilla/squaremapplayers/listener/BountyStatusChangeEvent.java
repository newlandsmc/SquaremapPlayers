package com.semivanilla.squaremapplayers.listener;

import com.semivanilla.bounties.api.enums.BountyStatus;
import com.semivanilla.bounties.api.events.BountyStatusChange;
import com.semivanilla.squaremapplayers.SquaremapPlayers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class BountyStatusChangeEvent implements Listener {

    private final SquaremapPlayers plugin;

    public BountyStatusChangeEvent(SquaremapPlayers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onStatusChange(BountyStatusChange event){
        if(event.getPlayer().hasMetadata("vanished"))
            return;

        if(event.getStatus() == BountyStatus.PLAYER_BECAME_BOUNTY){
            plugin.getSquaremapHook().updateStatusFor(event.getPlayer(), true);
        }else {
            plugin.getSquaremapHook().updateStatusFor(event.getPlayer(),false);
        }
    }


}
