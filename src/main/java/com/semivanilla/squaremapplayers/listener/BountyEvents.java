package com.semivanilla.squaremapplayers.listener;

import com.semivanilla.bounties.api.events.BountyNewKillEvent;
import com.semivanilla.bounties.api.events.PlayerBountyClearEvent;
import com.semivanilla.bounties.api.events.PlayerNewBountyEvent;
import com.semivanilla.squaremapplayers.SquaremapPlayers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class BountyEvents implements Listener {

    private SquaremapPlayers plugin;

    public BountyEvents(SquaremapPlayers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBountyCreateEvent(PlayerNewBountyEvent event){
        if(event.getBounty().getPlayer().isEmpty())
            return;

        if(event.getBounty().getPlayer().get().hasMetadata("vanished"))
            return;

        final UUID plId = event.getBounty().getPlayerUUID();
        plugin.getSquaremapHook().updateStatusFor(event.getBounty().getPlayer().get(), true);
    }

    @EventHandler
    public void onBountyClearEvent(PlayerBountyClearEvent event){
        final Player player = plugin.getServer().getPlayer(event.getUuid());

        if(player == null)
            return;

        if(player.hasMetadata("vanished"))
            return;

        plugin.getSquaremapHook().updateStatusFor(player,false);
    }

    @EventHandler
    public void onBountyUpdate(BountyNewKillEvent event){
        final Player player = plugin.getServer().getPlayer(event.getPlayerUID());

        if(player == null)
            return;

        if(player.hasMetadata("vanished"))
            return;

        plugin.getSquaremapHook().updateStatusFor(player,true);

    }
}
