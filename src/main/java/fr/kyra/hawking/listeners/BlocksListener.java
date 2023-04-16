package fr.kyra.hawking.listeners;

import fr.kyra.hawking.HawKing;
import fr.kyra.hawking.manager.RequestManager;
import fr.kyra.hawking.objects.CustomPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class BlocksListener implements Listener {

    public static HawKing hawKing = HawKing.getInstance();
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material material = event.getBlock().getType();

        if (!event.isCancelled() && hawKing.getAuthorizedBlocks().contains(material)) {
            CustomPlayer customPlayer = hawKing.getCustomPlayers().get(player.getUniqueId());
            customPlayer.incrementAuthorizedBlock(material, 1);
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HashMap<UUID, CustomPlayer> customPlayers = hawKing.getCustomPlayers();

        if (!customPlayers.containsKey(player.getUniqueId())) {
            RequestManager.registerPlayer(player.getUniqueId(), player.getName());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HashMap<UUID, CustomPlayer> customPlayers = hawKing.getCustomPlayers();
        CustomPlayer customPlayer = customPlayers.get(player.getUniqueId());

        RequestManager.updatePlayer(player.getUniqueId(), customPlayer);
        customPlayers.remove(player.getUniqueId());
    }

    @EventHandler
        public void onKickPlayerEvent(PlayerKickEvent event) {
        Player player = event.getPlayer();
        HashMap<UUID, CustomPlayer> customPlayers = hawKing.getCustomPlayers();
        CustomPlayer customPlayer = customPlayers.get(player.getUniqueId());

        RequestManager.updatePlayer(player.getUniqueId(), customPlayer);
        customPlayers.remove(player.getUniqueId());
    }
}

